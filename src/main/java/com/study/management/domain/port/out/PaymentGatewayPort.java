package com.study.management.domain.port.out;

import com.study.management.domain.model.Money;

/**
 * OUTPUT PORT — payment gateway contract.
 *
 * Adapters:
 *   UpiPaymentGatewayAdapter   (profile: !razorpay) — synchronous mock/demo
 *   RazorpayPaymentGatewayAdapter (profile: razorpay) — real Razorpay API, async
 */
public interface PaymentGatewayPort {

    /**
     * Initiates a payment and returns a provider-issued reference string.
     * For Razorpay this is the Razorpay orderId (order_XXXXXXXX).
     * For UPI demo this is a fake UPI_TXN_XXXXXXXX reference.
     */
    String initiatePayment(Money amount);

    /**
     * True when the gateway operates asynchronously (payment confirmed via webhook).
     * False means the mock/demo gateway confirms immediately.
     * PaymentApplicationService uses this to decide PAYMENT_PENDING vs immediate SUCCESS.
     */
    default boolean isAsynchronous() { return false; }

    /**
     * Verifies the Razorpay payment signature returned by the frontend checkout.
     * HMAC-SHA256(keySecret, orderId + "|" + paymentId) must equal signature.
     * Default (UPI) always returns true — not used outside Razorpay flow.
     */
    default boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        return true;
    }

    /**
     * Verifies the HMAC-SHA256 signature on a Razorpay server-to-server webhook body.
     * Default (UPI) always returns true — not used outside Razorpay flow.
     */
    default boolean verifyWebhookSignature(String rawBody, String razorpaySignatureHeader) {
        return true;
    }

    /**
     * Returns the public key (Razorpay key.id) needed by the frontend checkout widget.
     * Returns empty string for non-Razorpay adapters.
     */
    default String getPublicKey() { return ""; }
}
