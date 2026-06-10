package com.study.management.domain.port.out;

import com.study.management.domain.model.PromoCode;

import java.util.List;
import java.util.Optional;

/**
 * OUTPUT PORT — promo code persistence contract.
 * Implemented by PromoCodePersistenceAdapter in the infrastructure layer.
 */
public interface PromoCodePort {

    Optional<PromoCode> findByCode(String code);

    PromoCode save(PromoCode promoCode);

    List<PromoCode> findAll();
}