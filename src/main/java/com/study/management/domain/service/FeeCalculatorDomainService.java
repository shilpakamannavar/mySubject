package com.study.management.domain.service;

import com.study.management.domain.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * DOMAIN SERVICE — pure fee calculation logic.
 * No Spring annotations, no I/O. Easily unit-testable in isolation.
 *
 * Formula:
 *   baseFee = patients × visitsPerPatient × ₹500
 *   gst     = baseFee × 18%
 *   gross   = baseFee + gst
 *   discount per promo type (FULL / PERCENTAGE / FLAT)
 *   total   = gross − discount  (floor at ₹0)
 */
public class FeeCalculatorDomainService {

    private static final Money   FEE_PER_VISIT = Money.of(500);
    private static final BigDecimal GST_RATE   = new BigDecimal("0.18");

    /** Builds an unsaved Invoice. PaymentApplicationService sets paymentRef + status afterwards. */
    public Invoice buildInvoice(Study study, PromoCode promo) {
        Money baseFee = FEE_PER_VISIT
                .multiply(BigDecimal.valueOf(study.getNumberOfPatients()))
                .multiply(BigDecimal.valueOf(study.getTotalVisitsPerPatient()));

        Money gst   = baseFee.multiply(GST_RATE);
        Money gross = baseFee.add(gst);

        Money discount = (promo != null) ? computeDiscount(gross, promo) : Money.ZERO;
        Money total    = gross.subtract(discount);

        Invoice invoice = new Invoice();
        invoice.setBaseFee(baseFee.getAmount());
        invoice.setGst(gst.getAmount());
        invoice.setDiscountAmount(discount.getAmount());
        invoice.setTotalAmount(total.getAmount());
        invoice.setPromoCodeUsed(promo != null ? promo.getCode().toUpperCase() : null);
        invoice.setStatus(PaymentStatus.INITIATED);

        return invoice;
    }

    private Money computeDiscount(Money gross, PromoCode promo) {
        return switch (promo.getDiscountType()) {
            case FULL       -> gross;
            case PERCENTAGE -> gross.multiply(
                    promo.getDiscountValue().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
            case FLAT       -> {
                Money flat = Money.of(promo.getDiscountValue());
                yield flat.isGreaterThan(gross) ? gross : flat;
            }
        };
    }
}