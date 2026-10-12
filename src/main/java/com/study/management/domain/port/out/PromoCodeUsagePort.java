package com.study.management.domain.port.out;

import com.study.management.domain.model.PromoCodeUsageLog;

import java.util.List;

public interface PromoCodeUsagePort {
    PromoCodeUsageLog save(PromoCodeUsageLog log);
    List<PromoCodeUsageLog> findByPromoCode(String promoCode);
    List<PromoCodeUsageLog> findAll();
    List<PromoCodeUsageLog> findByCustomerId(Long customerId);
}