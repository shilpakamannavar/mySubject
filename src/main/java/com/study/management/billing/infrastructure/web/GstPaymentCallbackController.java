package com.study.management.billing.infrastructure.web;

import com.study.management.billing.domain.port.in.GstBillingUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Handles the Razorpay browser redirect after payment (only when callback_url is set on the order).
 * Verifies the payment, marks the invoice PAID, then redirects to the frontend billing page
 * so the React app can show the receipt — the browser never sees a raw JSON response.
 *
 * Active only under spring.profiles.active=razorpay.
 * Configure the redirect target via razorpay.callback.frontend-url in application.properties.
 */
@RestController
@RequestMapping("/api/billing/payment")
@Profile("razorpay")
public class GstPaymentCallbackController {

    private final GstBillingUseCase billingUseCase;

    @Value("${razorpay.callback.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    public GstPaymentCallbackController(GstBillingUseCase billingUseCase) {
        this.billingUseCase = billingUseCase;
    }

    @PostMapping("/callback")
    public void paymentCallback(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String paymentId = req.getParameter("razorpay_payment_id");
        String orderId   = req.getParameter("razorpay_order_id");

        if (orderId != null && paymentId != null) {
            try {
                billingUseCase.confirmPayment(orderId, paymentId);
            } catch (Exception ignored) {
                // Webhook may have already marked it PAID — idempotent, safe to ignore
            }
        }

        String redirectUrl = frontendUrl + "/billing"
                + "?razorpay_payment_id=" + (paymentId != null ? paymentId : "")
                + "&razorpay_order_id="   + (orderId   != null ? orderId   : "");

        res.sendRedirect(redirectUrl);
    }
}
