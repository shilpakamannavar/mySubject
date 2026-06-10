package com.study.management.domain.port.in;

import com.study.management.domain.model.PromoCode;

import java.util.List;

/**
 * INPUT PORT — promo code operations exposed to the web layer.
 */
public interface PromoCodeUseCase {

    /**
     * Validates the code and returns it if usable.
     * Throws IllegalArgumentException (400) if not found.
     * Throws IllegalStateException (409) if expired / inactive / maxed-out.
     */
    PromoCode validate(String code);

    List<PromoCode> getAll();
}