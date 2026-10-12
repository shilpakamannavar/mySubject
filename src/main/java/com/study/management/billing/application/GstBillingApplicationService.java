package com.study.management.billing.application;

import com.study.management.billing.domain.model.*;
import com.study.management.billing.domain.port.in.GstBillingUseCase;
import com.study.management.billing.domain.port.out.*;
import com.study.management.billing.domain.service.GstTaxEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * APPLICATION SERVICE — implements GstBillingUseCase (primary port).
 *
 * Checkout pipeline (single transaction):
 *   1. Validate GSTIN format + external registry check
 *   2. Verify GSTIN state code matches declared billing state
 *   3. Upsert GstBillingProfile
 *   4. Compute CGST/SGST or IGST via GstTaxEngine (pure domain logic)
 *   5. Allocate sequential invoice number
 *   6. Create DRAFT TaxInvoice
 *   7. Create Razorpay order with GSTIN + business name in notes metadata
 *   8. Persist and return
 */
@Service
@Transactional
public class GstBillingApplicationService implements GstBillingUseCase {

    private final GstinValidationPort        gstinValidator;
    private final GstBillingProfilePort      profilePort;
    private final TaxInvoicePort             invoicePort;
    private final GstPaymentGatewayPort      paymentGateway;
    private final TaxInvoiceNotificationPort notificationPort;
    private final GstTaxEngine               taxEngine;
    private final PiLookupPort               piLookup;
    private final HospitalLookupPort         hospitalLookup;
    private final SubscriptionActivationPort subscriptionActivation;

    /** 2-digit GST state code of the platform's registered entity (default: 27 = Maharashtra). */
    @Value("${billing.platform.state-code:27}")
    private String platformStateCode;

    @Value("${billing.service.sac-code:997331}")
    private String defaultSacCode;

    @Value("${billing.service.description:Clinical Study Management Platform Access}")
    private String defaultServiceDescription;

    public GstBillingApplicationService(GstinValidationPort        gstinValidator,
                                        GstBillingProfilePort      profilePort,
                                        TaxInvoicePort             invoicePort,
                                        GstPaymentGatewayPort      paymentGateway,
                                        TaxInvoiceNotificationPort notificationPort,
                                        GstTaxEngine               taxEngine,
                                        PiLookupPort               piLookup,
                                        HospitalLookupPort         hospitalLookup,
                                        SubscriptionActivationPort subscriptionActivation) {
        this.gstinValidator        = gstinValidator;
        this.profilePort           = profilePort;
        this.invoicePort           = invoicePort;
        this.paymentGateway        = paymentGateway;
        this.notificationPort      = notificationPort;
        this.taxEngine             = taxEngine;
        this.piLookup              = piLookup;
        this.hospitalLookup        = hospitalLookup;
        this.subscriptionActivation = subscriptionActivation;
    }

    // ── Input port implementation ──────────────────────────────────────────────

    @Override
    public GstinValidateResult validateGstin(String gstin, String declaredBillingState) {
        if (!taxEngine.isValidGstinFormat(gstin))
            return new GstinValidateResult(false, null, null,
                    "Invalid GSTIN format. Expected 15 characters matching the GST pattern.");

        String stateCode = taxEngine.extractStateCode(gstin);

        if (!taxEngine.isKnownStateCode(stateCode))
            return new GstinValidateResult(false, stateCode, null,
                    "Unknown state code '" + stateCode + "' in GSTIN.");

        String canonicalName = taxEngine.stateNameFor(stateCode);

        // State-code / declared-state consistency check (blocks faulty B2B filing)
        if (declaredBillingState != null && !declaredBillingState.isBlank()) {
            boolean matches = canonicalName.toUpperCase()
                    .contains(declaredBillingState.trim().toUpperCase())
                    || declaredBillingState.trim().toUpperCase().contains(canonicalName);
            if (!matches)
                return new GstinValidateResult(false, stateCode, canonicalName,
                        "GSTIN state code " + stateCode + " (" + canonicalName + ") does not match " +
                        "declared billing state '" + declaredBillingState + "'. " +
                        "Transaction blocked to prevent faulty GST filing.");
        }

        // External registry validation (format-only mock by default; real API in production)
        GstinValidationPort.GstinValidationResult external = gstinValidator.validate(gstin);
        if (!external.valid())
            return new GstinValidateResult(false, stateCode, canonicalName, external.errorMessage());

        return new GstinValidateResult(true, stateCode, canonicalName, null);
    }

    @Override
    public TaxInvoice initiateCheckout(CheckoutCommand cmd) {
        // ── 1. Validate GSTIN ──────────────────────────────────────────────────
        GstinValidateResult validation = validateGstin(cmd.gstin(), cmd.billingState());
        if (!validation.valid())
            throw new IllegalArgumentException("GSTIN validation failed: " + validation.errorMessage());

        // ── 2. Resolve PI or Hospital by phone number ─────────────────────────
        String resolvedEmail = cmd.businessEmail();
        UUID   resolvedPiId  = null;
        UUID   resolvedHospitalId = null;

        Optional<PiLookupPort.PiSummary> piOpt = piLookup.findByPhone(cmd.phoneNumber());
        if (piOpt.isPresent()) {
            PiLookupPort.PiSummary pi = piOpt.get();
            resolvedPiId = pi.piId();
            if (isBlank(resolvedEmail)) resolvedEmail = pi.email();
        } else {
            Optional<HospitalLookupPort.HospitalSummary> hospOpt =
                    hospitalLookup.findByPhone(cmd.phoneNumber());
            if (hospOpt.isPresent()) {
                HospitalLookupPort.HospitalSummary hospital = hospOpt.get();
                resolvedHospitalId = hospital.hospitalId();
                if (isBlank(resolvedEmail)) resolvedEmail = hospital.email();
            } else {
                throw new IllegalArgumentException(
                        "No Primary Investigator or Hospital/SMO found for phone: " + cmd.phoneNumber() +
                        ". Ensure the profile is registered at POST /api/v1/investigators or /api/v1/hospitals.");
            }
        }

        // ── 3. Upsert billing profile ──────────────────────────────────────────
        GstBillingProfile profile = profilePort.findByGstin(cmd.gstin())
                .orElseGet(GstBillingProfile::new);
        profile.setGstin(cmd.gstin());
        profile.setBusinessName(cmd.businessName());
        profile.setBillingState(cmd.billingState());
        profile.setRegisteredAddress(cmd.registeredAddress());
        profile.setBusinessEmail(resolvedEmail);
        profile.setPlaceOfSupplyCode(validation.stateCode());
        profile.setPiId(resolvedPiId);
        profile.setHospitalId(resolvedHospitalId);
        profile.setItcEligible(true);
        profile = profilePort.save(profile);

        // ── 5. Compute tax breakdown ───────────────────────────────────────────
        TaxType      taxType   = taxEngine.determineTaxType(validation.stateCode(), platformStateCode);
        TaxBreakdown breakdown = taxEngine.compute(cmd.baseAmount(), taxType);

        // ── 6. Build DRAFT invoice ─────────────────────────────────────────────
        LocalDate subscriptionStart = LocalDate.now();
        LocalDate subscriptionEnd   = subscriptionStart.plusMonths(cmd.subscriptionMonths()).minusDays(1);

        TaxInvoice invoice = new TaxInvoice();
        invoice.setInvoiceNumber(invoicePort.nextInvoiceNumber());
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setStatus(TaxInvoiceStatus.DRAFT);
        invoice.setTaxType(taxType);
        invoice.setBillingProfile(profile);
        invoice.setServiceDescription(
                isBlank(cmd.serviceDescription()) ? defaultServiceDescription : cmd.serviceDescription());
        invoice.setSacCode(isBlank(cmd.sacCode()) ? defaultSacCode : cmd.sacCode());
        invoice.setBaseAmount(cmd.baseAmount());
        invoice.setCgstRate(breakdown.cgstRate());
        invoice.setCgstAmount(breakdown.cgstAmount());
        invoice.setSgstRate(breakdown.sgstRate());
        invoice.setSgstAmount(breakdown.sgstAmount());
        invoice.setIgstRate(breakdown.igstRate());
        invoice.setIgstAmount(breakdown.igstAmount());
        invoice.setTotalAmount(breakdown.totalAmount());
        invoice.setPlaceOfSupply(validation.stateCode());
        invoice.setReferenceId(cmd.referenceId());
        invoice.setSubscriptionMonths(cmd.subscriptionMonths());
        invoice.setSubscriptionStartDate(subscriptionStart);
        invoice.setSubscriptionEndDate(subscriptionEnd);
        TaxInvoice saved = invoicePort.save(invoice);

        // ── 5. Create Razorpay order with GSTIN in notes ───────────────────────
        GstPaymentGatewayPort.GstOrderResult order = paymentGateway.createOrder(
                saved.getTotalAmount(), cmd.gstin(), cmd.businessName(), saved.getInvoiceNumber());
        saved.setRazorpayOrderId(order.orderId());
        return invoicePort.save(saved);
    }

    @Override
    public TaxInvoice confirmPayment(String razorpayOrderId, String razorpayPaymentId) {
        TaxInvoice invoice = requireByOrderId(razorpayOrderId);

        if (invoice.getStatus() == TaxInvoiceStatus.PAID) return invoice; // idempotent

        invoice.setStatus(TaxInvoiceStatus.PAID);
        invoice.setRazorpayPaymentId(razorpayPaymentId);
        invoice.setPaidAt(LocalDateTime.now());
        TaxInvoice saved = invoicePort.save(invoice);

        // Activate subscription so the PI's subject enrollment is unblocked for the covered months
        UUID piId = saved.getBillingProfile().getPiId();
        subscriptionActivation.activateForPi(
                piId,
                saved.getSubscriptionStartDate() != null ? saved.getSubscriptionStartDate() : LocalDate.now(),
                saved.getSubscriptionMonths() != null ? saved.getSubscriptionMonths() : 1,
                razorpayPaymentId);

        // Dispatch finalized invoice to business email
        notificationPort.dispatchInvoice(saved);
        return saved;
    }

    @Override
    public TaxInvoice failPayment(String razorpayOrderId) {
        TaxInvoice invoice = requireByOrderId(razorpayOrderId);
        invoice.setStatus(TaxInvoiceStatus.CANCELLED);
        return invoicePort.save(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public TaxInvoice getInvoice(String invoiceNumber) {
        return invoicePort.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new IllegalArgumentException("Tax invoice not found: " + invoiceNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public TaxInvoice getInvoiceByOrderId(String orderId) {
        return requireByOrderId(orderId);
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private TaxInvoice requireByOrderId(String orderId) {
        return invoicePort.findByRazorpayOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tax invoice not found for Razorpay order: " + orderId));
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
