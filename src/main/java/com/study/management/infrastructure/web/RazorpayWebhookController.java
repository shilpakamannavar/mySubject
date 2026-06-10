package com.study.management.infrastructure.web;

import com.study.management.domain.model.Study;
import com.study.management.domain.port.in.PaymentUseCase;
import com.study.management.domain.port.in.StudyUseCase;
import com.study.management.domain.port.out.PaymentGatewayPort;
import com.study.management.infrastructure.web.dto.RazorpayCallbackRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.json.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Handles two distinct Razorpay callbacks:
 *
 *   POST /api/razorpay/callback  — called by YOUR FRONTEND after the user pays.
 *                                  Contains orderId + paymentId + signature.
 *                                  Verifies signature, then confirms or fails the study.
 *
 *   POST /api/razorpay/webhook   — called by RAZORPAY SERVERS (server-to-server).
 *                                  Handles payment.captured and payment.failed events.
 *                                  Verifies HMAC of raw request body.
 */
@Tag(name = "Razorpay", description = "Razorpay frontend callback and server webhook endpoints")
@RestController
@RequestMapping("/api/razorpay")
@Profile("razorpay")
public class RazorpayWebhookController {

    private final PaymentUseCase paymentUseCase;
    private final StudyUseCase   studyUseCase;
    private final PaymentGatewayPort gateway;

    public RazorpayWebhookController(PaymentUseCase paymentUseCase,
                                     StudyUseCase studyUseCase,
                                     PaymentGatewayPort gateway) {
        this.paymentUseCase = paymentUseCase;
        this.studyUseCase   = studyUseCase;
        this.gateway        = gateway;
    }

    // ── 1. Frontend callback ───────────────────────────────────────────

    @Operation(
        summary     = "Razorpay frontend payment callback",
        description = "Called by your frontend after Razorpay checkout completes. " +
                      "Verifies the HMAC-SHA256 signature, then activates (or fails) the study. " +
                      "Success body: { razorpayOrderId, razorpayPaymentId, razorpaySignature }. " +
                      "Failure body: { razorpayOrderId } (paymentId and signature absent)."
    )
    @PostMapping("/callback")
    public ResponseEntity<Map<String, String>> handleFrontendCallback(
            @RequestBody RazorpayCallbackRequest callback) {

        Study study = studyUseCase.getByPaymentReference(callback.getRazorpayOrderId());

        if (!callback.isSuccess()) {
            paymentUseCase.failPayment(study.getId(), callback.getRazorpayOrderId());
            return ResponseEntity.ok(Map.of("status", "FAILED",
                    "message", "Payment failed. Please retry."));
        }

        boolean valid = gateway.verifyPaymentSignature(
                callback.getRazorpayOrderId(),
                callback.getRazorpayPaymentId(),
                callback.getRazorpaySignature());

        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "INVALID_SIGNATURE",
                            "message", "Payment signature verification failed."));
        }

        paymentUseCase.confirmPayment(study.getId(), callback.getRazorpayPaymentId());
        return ResponseEntity.ok(Map.of("status", "SUCCESS",
                "message", "Payment confirmed. Study is now ACTIVE.",
                "studyId", study.getStudyId()));
    }

    // ── 2. Server-to-server webhook ────────────────────────────────────

    @Operation(
        summary     = "Razorpay server webhook",
        description = "Called by Razorpay servers for payment.captured and payment.failed events. " +
                      "Verifies the X-Razorpay-Signature header before processing."
    )
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String rawBody,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {

        if (!gateway.verifyWebhookSignature(rawBody, signature))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid webhook signature");

        JSONObject payload  = new JSONObject(rawBody);
        String event        = payload.optString("event");
        JSONObject payment  = payload
                .optJSONObject("payload")
                .optJSONObject("payment")
                .optJSONObject("entity");

        if (payment == null)
            return ResponseEntity.ok("Unhandled webhook event: " + event);

        String orderId   = payment.optString("order_id");
        String paymentId = payment.optString("id");

        Study study;
        try {
            study = studyUseCase.getByPaymentReference(orderId);
        } catch (IllegalArgumentException e) {
            // Order not created by this service — ignore
            return ResponseEntity.ok("Order not found, ignored: " + orderId);
        }

        switch (event) {
            case "payment.captured" -> {
                paymentUseCase.confirmPayment(study.getId(), paymentId);
                return ResponseEntity.ok("Payment captured. Study activated: " + study.getStudyId());
            }
            case "payment.failed" -> {
                paymentUseCase.failPayment(study.getId(), paymentId);
                return ResponseEntity.ok("Payment failed recorded for study: " + study.getStudyId());
            }
            default -> { return ResponseEntity.ok("Unhandled event: " + event); }
        }
    }
}