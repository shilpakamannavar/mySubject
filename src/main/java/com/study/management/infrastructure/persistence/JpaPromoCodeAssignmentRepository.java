package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.PromoCode;
import com.study.management.domain.model.PromoCodeAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface JpaPromoCodeAssignmentRepository extends JpaRepository<PromoCodeAssignment, Long> {

    List<PromoCodeAssignment> findByPromoCode(PromoCode promoCode);

    Optional<PromoCodeAssignment> findByPromoCodeAndCustomerIsNull(PromoCode promoCode);

    Optional<PromoCodeAssignment> findByPromoCodeAndCustomer_Id(PromoCode promoCode, Long customerId);

    @Query("SELECT COUNT(a) > 0 FROM PromoCodeAssignment a " +
           "WHERE a.promoCode.id = :promoCodeId " +
           "AND (a.customer IS NULL OR a.customer.id = :customerId)")
    boolean isEligible(@Param("promoCodeId") Long promoCodeId,
                       @Param("customerId") Long customerId);
}