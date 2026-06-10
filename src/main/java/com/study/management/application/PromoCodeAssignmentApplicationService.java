package com.study.management.application;

import com.study.management.domain.model.Customer;
import com.study.management.domain.model.PromoCode;
import com.study.management.domain.model.PromoCodeAssignment;
import com.study.management.domain.model.PromoCodeUsageLog;
import com.study.management.domain.port.in.PromoCodeAssignmentUseCase;
import com.study.management.domain.port.out.CustomerPort;
import com.study.management.domain.port.out.PromoCodeAssignmentPort;
import com.study.management.domain.port.out.PromoCodePort;
import com.study.management.domain.port.out.PromoCodeUsagePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PromoCodeAssignmentApplicationService implements PromoCodeAssignmentUseCase {

    private final PromoCodePort promoCodePort;
    private final CustomerPort customerPort;
    private final PromoCodeAssignmentPort assignmentPort;
    private final PromoCodeUsagePort usagePort;

    public PromoCodeAssignmentApplicationService(PromoCodePort promoCodePort,
                                                 CustomerPort customerPort,
                                                 PromoCodeAssignmentPort assignmentPort,
                                                 PromoCodeUsagePort usagePort) {
        this.promoCodePort  = promoCodePort;
        this.customerPort   = customerPort;
        this.assignmentPort = assignmentPort;
        this.usagePort      = usagePort;
    }

    @Override
    public PromoCodeAssignment assignToCustomer(String promoCode, Long customerId) {
        PromoCode promo    = resolvePromoCode(promoCode);
        Customer customer  = customerPort.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        assignmentPort.findByPromoCodeAndCustomerId(promo, customerId).ifPresent(a -> {
            throw new IllegalStateException(
                    "Promo code '" + promoCode + "' is already assigned to customer " + customerId + ".");
        });

        PromoCodeAssignment assignment = new PromoCodeAssignment();
        assignment.setPromoCode(promo);
        assignment.setCustomer(customer);
        return assignmentPort.save(assignment);
    }

    @Override
    public PromoCodeAssignment assignToAll(String promoCode) {
        PromoCode promo = resolvePromoCode(promoCode);

        assignmentPort.findByPromoCodeAndCustomerIsNull(promo).ifPresent(a -> {
            throw new IllegalStateException(
                    "Promo code '" + promoCode + "' is already assigned to ALL customers.");
        });

        PromoCodeAssignment assignment = new PromoCodeAssignment();
        assignment.setPromoCode(promo);
        assignment.setCustomer(null); // null = ALL
        return assignmentPort.save(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromoCodeAssignment> getAssignments(String promoCode) {
        PromoCode promo = resolvePromoCode(promoCode);
        return assignmentPort.findByPromoCode(promo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromoCodeUsageLog> getUsageByPromoCode(String promoCode) {
        return usagePort.findByPromoCode(promoCode.trim().toUpperCase());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromoCodeUsageLog> getUsageByCustomer(Long customerId) {
        return usagePort.findByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromoCodeUsageLog> getAllUsage() {
        return usagePort.findAll();
    }

    private PromoCode resolvePromoCode(String code) {
        return promoCodePort.findByCode(code.trim())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Promo code '" + code + "' is not recognised."));
    }
}