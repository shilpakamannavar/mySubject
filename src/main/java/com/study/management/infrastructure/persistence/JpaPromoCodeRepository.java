package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Internal Spring Data repository for promo codes. */
public interface JpaPromoCodeRepository extends JpaRepository<PromoCode, Long> {

    Optional<PromoCode> findByCodeIgnoreCase(String code);
}