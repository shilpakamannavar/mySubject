package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.PromoCode;
import com.study.management.domain.port.out.PromoCodePort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * OUTPUT PORT ADAPTER — bridges PromoCodePort to JPA.
 */
@Repository
public class PromoCodePersistenceAdapter implements PromoCodePort {

    private final JpaPromoCodeRepository jpa;

    public PromoCodePersistenceAdapter(JpaPromoCodeRepository jpa) {
        this.jpa = jpa;
    }

    @Override public Optional<PromoCode> findByCode(String code) { return jpa.findByCodeIgnoreCase(code); }
    @Override public PromoCode           save(PromoCode p)       { return jpa.save(p); }
    @Override public List<PromoCode>     findAll()               { return jpa.findAll(); }
}