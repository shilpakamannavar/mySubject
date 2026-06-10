package com.study.management.application;

import com.study.management.domain.model.*;
import com.study.management.domain.port.in.PaymentUseCase;
import com.study.management.domain.port.out.PaymentGatewayPort;
import com.study.management.domain.port.out.PromoCodeAssignmentPort;
import com.study.management.domain.port.out.PromoCodePort;
import com.study.management.domain.port.out.PromoCodeUsagePort;
import com.study.management.domain.port.out.StudyPort;
import com.study.management.domain.service.FeeCalculatorDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * APPLICATION SERVICE — implements PaymentUseCase (input port).
 *
 * Orchestrates in a single transaction:
 *   1. Load study, validate DRAFT status
 *   2. Validate promo code (if supplied)
 *   3. Ask FeeCalculatorDomainService to build invoice (pure domain logic)
 *   4. Route: FULL promo → waive | otherwise → call payment gateway
 *   5. Activate study, increment promo usage, persist
 */
@Service
@Transactional
public class PaymentApplicationService implements PaymentUseCase {

    private final StudyPort studyPort;
    private final PromoCodePort promoCodePort;
    private final FeeCalculatorDomainService feeCalculator;
    private final PaymentGatewayPort paymentGateway;
    private final PromoCodeAssignmentPort assignmentPort;
    private final PromoCodeUsagePort usagePort;

    public PaymentApplicationService(StudyPort studyPort,
                                     PromoCodePort promoCodePort,
                                     FeeCalculatorDomainService feeCalculator,
                                     PaymentGatewayPort paymentGateway,
                                     PromoCodeAssignmentPort assignmentPort,
                                     PromoCodeUsagePort usagePort) {
        this.studyPort      = studyPort;
        this.promoCodePort  = promoCodePort;
        this.feeCalculator  = feeCalculator;
        this.paymentGateway = paymentGateway;
        this.assignmentPort = assignmentPort;
        this.usagePort      = usagePort;
    }

    // ── Input port implementation ──────────────────────────────────────

    @Override
    public Invoice proceedForPayment(Long studyId, String promoCode) {
        Study study = studyPort.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("Study not found: " + studyId));

        if (study.getStatus() != StudyStatus.DRAFT && study.getStatus() != StudyStatus.INACTIVE)
            throw new IllegalStateException(
                    "Only INACTIVE studies can proceed to payment. Current status: " + study.getStatus());

        // ── 1. Validate promo (output port) ──────────────────────────
        PromoCode promo = resolvePromo(promoCode);
        checkPromoEligibility(promo, study);

        // ── 2. Domain service builds invoice (pure logic, no I/O) ────
        Invoice invoice = feeCalculator.buildInvoice(study, promo);
        invoice.setStudy(study);
        study.setInvoice(invoice);

        // ── 3. Route payment ─────────────────────────────────────────
        Money total = Money.of(invoice.getTotalAmount());

        if (total.isZero()) {
            // FULL promo — waive completely, skip gateway, activate immediately
            invoice.setPaymentReference("PROMO_WAIVED_" + promo.getCode().toUpperCase());
            invoice.setStatus(PaymentStatus.SUCCESS);
            study.setStatus(StudyStatus.ACTIVE);

        } else if (paymentGateway.isAsynchronous()) {
            // Razorpay (and other async gateways) — create order, wait for webhook
            String orderId = paymentGateway.initiatePayment(total);
            invoice.setPaymentReference(orderId);
            invoice.setStatus(PaymentStatus.INITIATED);
            study.setStatus(StudyStatus.PAYMENT_PENDING);

        } else {
            // UPI demo / sync gateway — treat as immediate success
            invoice.setPaymentReference(paymentGateway.initiatePayment(total));
            invoice.setStatus(PaymentStatus.SUCCESS);
            study.setStatus(StudyStatus.ACTIVE);
        }

        // ── 4. Increment promo usage counter + write audit log ───────
        if (promo != null) {
            promo.setUsedCount(promo.getUsedCount() + 1);
            promoCodePort.save(promo);
            writeUsageLog(promo, study, invoice);
        }

        // ── 5. Persist (cascades: saves invoice) ─────────────────────
        // Use the returned entity — JPA populates the generated IDs (invoice.id) on it.
        Study saved = studyPort.save(study);
        return saved.getInvoice();
    }

    @Override
    public void confirmPayment(Long studyId, String paymentRef) {
        Study study = studyPort.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("Study not found: " + studyId));

        if (study.getStatus() != StudyStatus.PAYMENT_PENDING)
            throw new IllegalStateException("Study is not awaiting payment confirmation.");

        Invoice invoice = study.getInvoice();
        invoice.setPaymentReference(paymentRef);
        invoice.setStatus(PaymentStatus.SUCCESS);
        study.setStatus(StudyStatus.ACTIVE);
        studyPort.save(study);
    }

    @Override
    public void failPayment(Long studyId, String paymentRef) {
        Study study = studyPort.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("Study not found: " + studyId));

        Invoice invoice = study.getInvoice();
        invoice.setPaymentReference(paymentRef);
        invoice.setStatus(PaymentStatus.FAILED);
        // status stays PAYMENT_PENDING — user may retry
        studyPort.save(study);
    }

    // ── Private helpers ────────────────────────────────────────────────

    private void checkPromoEligibility(PromoCode promo, Study study) {
        if (promo == null) return;
        Customer customer = study.getCustomer();
        if (customer == null) return; // pre-customer studies bypass the check
        if (!assignmentPort.isEligible(promo.getId(), customer.getId()))
            throw new IllegalStateException(
                    "Promo code '" + promo.getCode() + "' has not been provisioned for customer '"
                    + customer.getName() + "'. Contact admin.");
    }

    private void writeUsageLog(PromoCode promo, Study study, Invoice invoice) {
        PromoCodeUsageLog log = new PromoCodeUsageLog();
        log.setPromoCode(promo.getCode());
        log.setStudyDbId(study.getId());
        log.setStudyBusinessId(study.getStudyId());
        log.setDiscountAmount(invoice.getDiscountAmount());
        Customer customer = study.getCustomer();
        if (customer != null) {
            log.setCustomer(customer);
            log.setCustomerName(customer.getName());
        }
        usagePort.save(log);
    }

    private PromoCode resolvePromo(String code) {
        if (code == null || code.isBlank()) return null;

        PromoCode promo = promoCodePort.findByCode(code.trim())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Promo code '" + code + "' is not recognised."));

        if (!promo.isActive())
            throw new IllegalStateException("Promo code '" + code + "' is inactive.");

        if (promo.getExpiryDate() != null && promo.getExpiryDate().isBefore(LocalDate.now()))
            throw new IllegalStateException(
                    "Promo code '" + code + "' expired on " + promo.getExpiryDate() + ".");

        if (promo.getMaxUsages() != null && promo.getUsedCount() >= promo.getMaxUsages())
            throw new IllegalStateException(
                    "Promo code '" + code + "' has reached its limit of " + promo.getMaxUsages() + " use(s).");

        return promo;
    }
}