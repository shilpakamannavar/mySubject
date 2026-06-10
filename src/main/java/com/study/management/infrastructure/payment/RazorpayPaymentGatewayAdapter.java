package com.study.management.infrastructure.payment;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.study.management.domain.model.Money;
import com.study.management.domain.port.out.PaymentGatewayPort;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Razorpay payment gateway adapter.
 * Active only when spring.profiles.active=razorpay.
 *
 * Flow:
 *   1. initiatePayment()  → creates a Razorpay order, returns orderId
 *   2. Frontend renders Razorpay checkout using orderId + keyId
 *   3. User pays → frontend receives { orderId, paymentId, signature }
 *   4. verifyPaymentSignature() confirms the payment is authentic
 *   5. verifyWebhookSignature() used for server-to-server webhook events
 */
@Component
@Profile("razorpay")
public class RazorpayPaymentGatewayAdapter implements PaymentGatewayPort {

    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Value("${razorpay.webhook.secret:}")
    private String webhookSecret;

    @Value("${razorpay.currency:INR}")
    private String currency;

    public RazorpayPaymentGatewayAdapter(RazorpayClient razorpayClient) {
        this.razorpayClient = razorpayClient;
    }

    /**
     * Creates a Razorpay order and returns the orderId.
     * Amount is converted from ₹ to paise (multiply × 100).
     */
    @Override
    public String initiatePayment(Money amount) {
        try {
            // Razorpay requires integer paise; ₹100.50 → 10050
            int amountInPaise = amount.getAmount()
                    .multiply(BigDecimal.valueOf(100))
                    .intValue();

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", currency);
            orderRequest.put("payment_capture", 1); // auto-capture on payment

            Order order = razorpayClient.orders.create(orderRequest);
            return order.get("id"); // order_XXXXXXXXXXXXXXXX
        } catch (RazorpayException e) {
            throw new IllegalStateException("Razorpay order creation failed: " + e.getMessage(), e);
        }
    }

    /** Razorpay is asynchronous — study stays PAYMENT_PENDING until webhook confirms. */
    @Override
    public boolean isAsynchronous() { return true; }

    /**
     * Verifies the payment signature returned by Razorpay frontend checkout.
     * Formula: HMAC-SHA256(keySecret, orderId + "|" + paymentId) == signature
     */
    @Override
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        try {
            JSONObject attributes = new JSONObject()
                    .put("razorpay_order_id", orderId)
                    .put("razorpay_payment_id", paymentId)
                    .put("razorpay_signature", signature);
            Utils.verifyPaymentSignature(attributes, keySecret);
            return true;
        } catch (RazorpayException e) {
            return false;
        }
    }

    /**
     * Verifies the HMAC-SHA256 signature on a server-to-server Razorpay webhook.
     * Formula: HMAC-SHA256(webhookSecret, rawBody) == X-Razorpay-Signature header
     */
    @Override
    public boolean verifyWebhookSignature(String rawBody, String razorpaySignatureHeader) {
        if (webhookSecret == null || webhookSecret.isBlank()) return true; // skip in test
        try {
            Utils.verifyWebhookSignature(rawBody, razorpaySignatureHeader, webhookSecret);
            return true;
        } catch (RazorpayException e) {
            return false;
        }
    }

    /** Returns the Razorpay key.id — safe to expose to the frontend checkout widget. */
    @Override
    public String getPublicKey() { return keyId; }
}
