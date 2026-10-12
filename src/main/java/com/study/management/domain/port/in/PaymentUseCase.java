package com.study.management.domain.port.in;

import com.study.management.domain.model.Invoice;

/**
 * INPUT PORT — payment lifecycle.
 * Covers the main flow (proceed-payment) and the async webhook callbacks.
 */
public interface PaymentUseCase {

    /**
     * Step 3: validate optional promo, calculate fee + GST, route to UPI or waive.
     * Returns the saved invoice (study status becomes ACTIVE on success).
     */
    Invoice proceedForPayment(Long studyId, String promoCode);

    /** Webhook — UPI provider confirms success → study ACTIVE */
    void confirmPayment(Long studyId, String paymentRef);

    /** Webhook — UPI provider reports failure → study stays PAYMENT_PENDING */
    void failPayment(Long studyId, String paymentRef);
}