package com.study.management.billing.infrastructure.subscription;

import com.study.management.billing.domain.port.out.SubscriptionActivationPort;
import com.study.management.domain.model.SubscriptionStatus;
import com.study.management.domain.port.in.StudySubscriptionUseCase;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Bridges the billing module to the subscription domain.
 * Called once per invoice when Razorpay confirms payment.
 */
@Component
public class SubscriptionActivationAdapter implements SubscriptionActivationPort {

    private final StudySubscriptionUseCase subscriptionUseCase;

    public SubscriptionActivationAdapter(StudySubscriptionUseCase subscriptionUseCase) {
        this.subscriptionUseCase = subscriptionUseCase;
    }

    @Override
    public void activateForPi(UUID piId, LocalDate startDate, int subscriptionMonths, String paymentReference) {
        if (piId == null) return;

        // Idempotent: payPiCurrentMonthSubscription throws if already PAID for this period —
        // catch and swallow so webhook retries do not fail.
        try {
            var current = subscriptionUseCase.getPiCurrentMonthSubscription(piId);
            if (current.getStatus() == SubscriptionStatus.PAID
                    && current.getSubscriptionStartDate() != null
                    && !startDate.isBefore(current.getSubscriptionStartDate())
                    && !startDate.isAfter(current.getSubscriptionEndDate())) {
                return; // already activated, skip
            }
            subscriptionUseCase.payPiCurrentMonthSubscription(
                    piId, "razorpay-webhook", paymentReference, startDate, subscriptionMonths);
        } catch (IllegalStateException ignored) {
            // subscription already paid for this period — idempotent, safe to ignore
        }
    }
}
