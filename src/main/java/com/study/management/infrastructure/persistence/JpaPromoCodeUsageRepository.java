package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.PromoCodeUsageLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface JpaPromoCodeUsageRepository extends JpaRepository<PromoCodeUsageLog, Long> {
    List<PromoCodeUsageLog> findByPromoCodeIgnoreCase(String promoCode);
    List<PromoCodeUsageLog> findByCustomer_Id(Long customerId);
}