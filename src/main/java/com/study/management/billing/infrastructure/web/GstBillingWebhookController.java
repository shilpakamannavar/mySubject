package com.study.management.billing.infrastructure.web;

import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.study.management.billing.domain.model.TaxInvoice;
import com.study.management.billing.domain.port.in.GstBillingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Server-to-server Razorpay webhook for B2B GST invoices.
 * Active only under spring.profiles.active=razorpay.
 *
 * On order.paid  → confirms payment, marks invoice PAID, dispatches PDF to business email.
 * On payment.failed → marks invoice CANCELLED so the doctor can retry.
 *
 * Register this URL in the Razorpay Dashboard:
 *   https://your-domain/api/billing/webhook/razorpay
 */
@Tag(name = "GST Billing", description = "B2B GST invoice and Razorpay payment endpoints for doctors/hospitals")
@RestController
@RequestMapping("/api/billing/webhook")
@Profile("razorpay")
public class GstBillingWebhookController {

    private final GstBillingUseCase billingUseCase;

    @Value("${razorpay.webhook.secret:}")
    private String webhookSecret;

    public GstBillingWebhookController(GstBillingUseCase billingUseCase) {
        this.billingUseCase = billingUseCase;
    }

    @Operation(
        summary     = "Razorpay webhook for GST invoices",
        description = "Handles payment.captured and payment.failed events. " +
                      "Verifies the X-Razorpay-Signature HMAC before processing. " +
                      "On success: marks invoice PAID and dispatches it to the doctor's business email."
    )
    @PostMapping("/razorpay")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String rawBody,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {

        if (!verifySignature(rawBody, signature))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid webhook signature");

        JSONObject payload = new JSONObject(rawBody);
        String event       = payload.optString("event");

        JSONObject paymentObj = payload
                .optJSONObject("payload") != null
                ? payload.optJSONObject("payload").optJSONObject("payment")
                : null;
        JSONObject payment = (paymentObj != null) ? paymentObj.optJSONObject("entity") : null;

        if (payment == null)
            return ResponseEntity.ok("Unhandled event (no payment entity): " + event);

        String orderId   = payment.optString("order_id");
        String paymentId = payment.optString("id");

        TaxInvoice invoice;
        try {
            invoice = billingUseCase.getInvoiceByOrderId(orderId);
        } catch (IllegalArgumentException e) {
            // Order not created by this billing module — ignore silently
            return ResponseEntity.ok("Order not in billing module, ignored: " + orderId);
        }

        return switch (event) {
            case "payment.captured" -> {
                billingUseCase.confirmPayment(orderId, paymentId);
                yield ResponseEntity.ok("Invoice " + invoice.getInvoiceNumber() + " marked PAID. Invoice dispatched.");
            }
            case "payment.failed" -> {
                billingUseCase.failPayment(orderId);
                yield ResponseEntity.ok("Invoice " + invoice.getInvoiceNumber() + " marked CANCELLED.");
            }
            default -> ResponseEntity.ok("Unhandled event: " + event);
        };
    }

    private boolean verifySignature(String rawBody, String signature) {
        if (webhookSecret == null || webhookSecret.isBlank()) return true; // skip in test/dev
        try {
            Utils.verifyWebhookSignature(rawBody, signature, webhookSecret);
            return true;
        } catch (RazorpayException e) {
            return false;
        }
    }
}
