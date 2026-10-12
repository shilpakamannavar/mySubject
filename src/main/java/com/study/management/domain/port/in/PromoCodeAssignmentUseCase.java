package com.study.management.domain.port.in;

import com.study.management.domain.model.PromoCodeAssignment;
import com.study.management.domain.model.PromoCodeUsageLog;

import java.util.List;

public interface PromoCodeAssignmentUseCase {

    /** Assign a promo code to one specific customer. */
    PromoCodeAssignment assignToCustomer(String promoCode, Long customerId);

    /** Assign a promo code to ALL customers (global grant). */
    PromoCodeAssignment assignToAll(String promoCode);

    /** List every assignment (specific + global) for a promo code. */
    List<PromoCodeAssignment> getAssignments(String promoCode);

    /** Usage log filtered by promo code. */
    List<PromoCodeUsageLog> getUsageByPromoCode(String promoCode);

    /** Usage log filtered by customer. */
    List<PromoCodeUsageLog> getUsageByCustomer(Long customerId);

    /** Full usage log — all codes, all customers. */
    List<PromoCodeUsageLog> getAllUsage();
}