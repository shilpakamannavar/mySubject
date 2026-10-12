package com.study.management.billing.domain.port.out;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Outbound port: called by the billing module when a TaxInvoice transitions to PAID.
 * Activates a StudySubscription for the PI so that subject enrollment is unblocked
 * for the covered period.
 *
 * No-op when piId is null (hospital-only invoice with no PI link).
 */
public interface SubscriptionActivationPort {

    /**
     * @param piId             PI to activate subscription for; null = skip
     * @param startDate        first day of the subscription period
     * @param subscriptionMonths number of months covered
     * @param paymentReference Razorpay payment ID or invoice number for audit
     */
    void activateForPi(UUID piId, LocalDate startDate, int subscriptionMonths, String paymentReference);
}
