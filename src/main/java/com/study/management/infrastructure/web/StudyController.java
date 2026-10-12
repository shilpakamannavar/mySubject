package com.study.management.infrastructure.web;

import com.study.management.domain.model.Invoice;
import com.study.management.domain.model.PaymentStatus;
import com.study.management.domain.model.Study;
import com.study.management.domain.model.StudyVisit;
import com.study.management.domain.port.in.PaymentUseCase;
import com.study.management.domain.port.in.StudyUseCase;
import com.study.management.domain.port.out.PaymentGatewayPort;
import com.study.management.infrastructure.web.dto.InvoiceResponse;
import com.study.management.infrastructure.web.dto.PaymentCallbackRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * WEB ADAPTER — depends only on input-port INTERFACES (StudyUseCase, PaymentUseCase).
 * No concrete service class is imported here. This is the key hexagonal benefit:
 * the web layer is completely decoupled from the application/domain layers.
 */
@Tag(name = "Studies", description = "Clinical trial study lifecycle: DRAFT → PAYMENT_PENDING / ACTIVE")
@RestController
@RequestMapping("/api/studies")
public class StudyController {

    private final StudyUseCase      studyUseCase;
    private final PaymentUseCase    paymentUseCase;
    private final PaymentGatewayPort gateway;

    public StudyController(StudyUseCase studyUseCase,
                           PaymentUseCase paymentUseCase,
                           PaymentGatewayPort gateway) {
        this.studyUseCase   = studyUseCase;
        this.paymentUseCase = paymentUseCase;
        this.gateway        = gateway;
    }

    // ─── Step 1 ───────────────────────────────────────────────────────

    @Operation(
        summary  = "Create study & auto-generate visit rows",
        description = "Saves the study header and immediately generates N empty visit rows " +
                      "(one per visit slot). Study status = DRAFT."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Study created; visit rows auto-generated"),
        @ApiResponse(responseCode = "400", description = "Duplicate studyId or validation error",
                     content = @Content(schema = @Schema(example = "{\"error\":\"Study ID already exists\"}")))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'CRC', 'CO_INVESTIGATOR', 'PRIMARY_INVESTIGATOR')")
    @PostMapping
    public ResponseEntity<Study> createStudy(
            @Valid @RequestBody Study study,
            @Parameter(description = "Optional customer ID to associate the study with a customer")
            @RequestParam(required = false) Long customerId,
            @AuthenticationPrincipal Jwt jwt) {
        study.setCreatedByPhone(jwt.getClaimAsString("phone_number"));
        return ResponseEntity.status(HttpStatus.CREATED).body(studyUseCase.createStudy(study, customerId));
    }

    // ─── Step 2 ───────────────────────────────────────────────────────

    @Operation(
        summary     = "Save visit details",
        description = "User fills editable columns of the auto-generated visit table. " +
                      "visitNumber is locked — only type, duration, window, and instructions can be changed."
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'CRC', 'CO_INVESTIGATOR')")
    @PutMapping("/{id}/visits")
    public ResponseEntity<Study> updateVisits(
            @PathVariable Long id,
            @RequestBody List<StudyVisit> visits) {
        return ResponseEntity.ok(studyUseCase.updateVisits(id, visits));
    }

    // ─── Step 3 ───────────────────────────────────────────────────────

    @Operation(
        summary     = "Proceed to payment (SAVE & PROCEED FOR PAYMENT)",
        description = "Calculates base fee + 18% GST. Optional promoCode:\n" +
                      "- **FULL** discount → totalAmount = ₹0, payment gateway skipped, study activates immediately.\n" +
                      "- **UPI (demo)** → study activates immediately with a mock reference.\n" +
                      "- **Razorpay** → response contains `razorpayOrderId` + `razorpayKeyId`; " +
                      "render the Razorpay checkout widget, then POST to `/api/razorpay/callback`."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invoice created and study activated"),
        @ApiResponse(responseCode = "400", description = "Promo code not found",
                     content = @Content(schema = @Schema(example = "{\"error\":\"Promo code 'XYZ' is not recognised.\"}"))),
        @ApiResponse(responseCode = "409", description = "Study not in DRAFT / promo expired or maxed-out",
                     content = @Content(schema = @Schema(example = "{\"error\":\"Promo code 'EXPIRED24' expired on 2024-12-31.\"}")))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/proceed-payment")
    public ResponseEntity<InvoiceResponse> proceedForPayment(
            @PathVariable Long id,
            @Parameter(description = "Optional promo code. FULL type bypasses payment entirely.")
            @RequestParam(required = false) String promoCode) {

        String studyStringId = studyUseCase.getById(id).getStudyId();
        Invoice invoice      = paymentUseCase.proceedForPayment(id, promoCode);
        return ResponseEntity.ok(toInvoiceResponse(invoice, studyStringId));
    }

    // ─── Payment gateway webhook ───────────────────────────────────────

    @Operation(
        summary     = "Payment callback (UPI webhook)",
        description = "Called asynchronously by the payment provider with the final outcome."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/payment-callback")
    public ResponseEntity<String> paymentCallback(
            @PathVariable Long id,
            @RequestBody PaymentCallbackRequest callback) {

        if (callback.isSuccess()) {
            paymentUseCase.confirmPayment(id, callback.getPaymentReference());
            return ResponseEntity.ok("Payment confirmed. Study is now ACTIVE.");
        } else {
            paymentUseCase.failPayment(id, callback.getPaymentReference());
            return ResponseEntity.ok("Payment failed. Please retry.");
        }
    }

    // ─── My Studies dashboard ─────────────────────────────────────────

    @Operation(
        summary     = "My Studies dashboard",
        description = "Returns all studies created by the authenticated primary investigator."
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR')")
    @GetMapping("/my-studies")
    public ResponseEntity<List<Study>> getMyStudies(@AuthenticationPrincipal Jwt jwt) {
        String phone = jwt.getClaimAsString("phone_number");
        return ResponseEntity.ok(studyUseCase.getStudiesByPiPhone(phone));
    }

    @Operation(summary = "Get study by internal DB id")
    @GetMapping("/{id}")
    public ResponseEntity<Study> getStudy(@PathVariable Long id) {
        return ResponseEntity.ok(studyUseCase.getById(id));
    }

    @Operation(summary = "All studies (dashboard table)")
    @GetMapping
    public ResponseEntity<List<Study>> getAllStudies() {
        return ResponseEntity.ok(studyUseCase.getAllStudies());
    }

    @Operation(summary = "Fee breakdown: base + 18% GST")
    @GetMapping("/{id}/fee")
    public ResponseEntity<?> getFee(@PathVariable Long id) {
        return ResponseEntity.ok(studyUseCase.calculateFee(id));
    }

    @Operation(summary = "Activate study (payment must be complete)")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Study> activate(@PathVariable Long id) {
        return ResponseEntity.ok(studyUseCase.activateStudy(id));
    }

    @Operation(summary = "Disable study and revoke access")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/disable")
    public ResponseEntity<Study> disable(@PathVariable Long id) {
        return ResponseEntity.ok(studyUseCase.disableStudy(id));
    }

    // ── Helper ────────────────────────────────────────────────────────

    private InvoiceResponse toInvoiceResponse(Invoice invoice, String studyId) {
        InvoiceResponse r = new InvoiceResponse();
        r.setInvoiceId(invoice.getId());
        r.setStudyId(studyId);
        r.setBaseFee(invoice.getBaseFee());
        r.setGst(invoice.getGst());
        r.setDiscountAmount(invoice.getDiscountAmount());
        r.setTotalAmount(invoice.getTotalAmount());
        r.setPromoCodeUsed(invoice.getPromoCodeUsed());
        r.setPromoApplied(invoice.getPromoCodeUsed() != null);
        r.setPaymentReference(invoice.getPaymentReference());
        r.setPaymentStatus(invoice.getStatus());

        // Razorpay-specific: populate checkout fields when gateway is async and payment pending
        if (gateway.isAsynchronous() && invoice.getStatus() == PaymentStatus.INITIATED) {
            r.setRazorpayOrderId(invoice.getPaymentReference());
            r.setRazorpayKeyId(gateway.getPublicKey());
            r.setRazorpayCurrency("INR");
            r.setRequiresCheckout(true);
        }

        return r;
    }
}