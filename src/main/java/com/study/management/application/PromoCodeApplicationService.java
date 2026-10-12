package com.study.management.application;

import com.study.management.domain.model.PromoCode;
import com.study.management.domain.port.in.PromoCodeUseCase;
import com.study.management.domain.port.out.PromoCodePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * APPLICATION SERVICE — implements PromoCodeUseCase (input port).
 */
@Service
@Transactional(readOnly = true)
public class PromoCodeApplicationService implements PromoCodeUseCase {

    private final PromoCodePort promoCodePort;

    public PromoCodeApplicationService(PromoCodePort promoCodePort) {
        this.promoCodePort = promoCodePort;
    }

    @Override
    @Transactional
    public PromoCode create(PromoCode promoCode) {
        promoCode.setCode(promoCode.getCode().trim().toUpperCase());
        promoCode.setUsedCount(0);
        promoCode.setActive(true);
        return promoCodePort.save(promoCode);
    }

    @Override
    public PromoCode validate(String code) {
        PromoCode promo = promoCodePort.findByCode(code.trim())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Promo code '" + code + "' is not recognised."));

        if (!promo.isActive())
            throw new IllegalStateException("Promo code '" + code + "' is inactive.");

        if (promo.getExpiryDate() != null && promo.getExpiryDate().isBefore(LocalDate.now()))
            throw new IllegalStateException(
                    "Promo code '" + code + "' expired on " + promo.getExpiryDate() + ".");

        if (promo.getMaxUsages() != null && promo.getUsedCount() >= promo.getMaxUsages())
            throw new IllegalStateException(
                    "Promo code '" + code + "' has reached its maximum of "
                    + promo.getMaxUsages() + " use(s).");

        return promo;
    }

    @Override
    public List<PromoCode> getAll() {
        return promoCodePort.findAll();
    }
}