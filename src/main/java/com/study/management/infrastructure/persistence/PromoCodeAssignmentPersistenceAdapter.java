package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.PromoCode;
import com.study.management.domain.model.PromoCodeAssignment;
import com.study.management.domain.port.out.PromoCodeAssignmentPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PromoCodeAssignmentPersistenceAdapter implements PromoCodeAssignmentPort {

    private final JpaPromoCodeAssignmentRepository jpa;

    public PromoCodeAssignmentPersistenceAdapter(JpaPromoCodeAssignmentRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public PromoCodeAssignment save(PromoCodeAssignment a) { return jpa.save(a); }

    @Override
    public List<PromoCodeAssignment> findByPromoCode(PromoCode promoCode) {
        return jpa.findByPromoCode(promoCode);
    }

    @Override
    public boolean isEligible(Long promoCodeId, Long customerId) {
        return jpa.isEligible(promoCodeId, customerId);
    }

    @Override
    public Optional<PromoCodeAssignment> findByPromoCodeAndCustomerIsNull(PromoCode promoCode) {
        return jpa.findByPromoCodeAndCustomerIsNull(promoCode);
    }

    @Override
    public Optional<PromoCodeAssignment> findByPromoCodeAndCustomerId(PromoCode promoCode, Long customerId) {
        return jpa.findByPromoCodeAndCustomer_Id(promoCode, customerId);
    }
}