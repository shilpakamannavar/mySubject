package com.study.management.domain.port.out;

import com.study.management.domain.model.PromoCode;
import com.study.management.domain.model.PromoCodeAssignment;

import java.util.List;
import java.util.Optional;

public interface PromoCodeAssignmentPort {

    PromoCodeAssignment save(PromoCodeAssignment assignment);

    /** All assignments (specific + global) for a given promo code. */
    List<PromoCodeAssignment> findByPromoCode(PromoCode promoCode);

    /** True if there is a SPECIFIC assignment for this (promoCode, customer)
     *  OR a GLOBAL assignment (customer = null) for this promoCode. */
    boolean isEligible(Long promoCodeId, Long customerId);

    Optional<PromoCodeAssignment> findByPromoCodeAndCustomerIsNull(PromoCode promoCode);

    Optional<PromoCodeAssignment> findByPromoCodeAndCustomerId(PromoCode promoCode, Long customerId);
}