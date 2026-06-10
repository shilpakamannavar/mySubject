package com.study.management.infrastructure.web;

import com.study.management.domain.model.PromoCode;
import com.study.management.domain.model.PromoCodeAssignment;
import com.study.management.domain.model.PromoCodeUsageLog;
import com.study.management.domain.port.in.PromoCodeAssignmentUseCase;
import com.study.management.domain.port.in.PromoCodeUseCase;
import com.study.management.infrastructure.web.dto.PromoCodeAssignmentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Promo Codes", description = "Validate, provision, and track promo codes for study fee discounts")
@RestController
@RequestMapping("/api/promo-codes")
public class PromoCodeController {

    private final PromoCodeUseCase promoCodeUseCase;
    private final PromoCodeAssignmentUseCase assignmentUseCase;

    public PromoCodeController(PromoCodeUseCase promoCodeUseCase,
                               PromoCodeAssignmentUseCase assignmentUseCase) {
        this.promoCodeUseCase = promoCodeUseCase;
        this.assignmentUseCase = assignmentUseCase;
    }

    @Operation(
        summary     = "Validate a promo code",
        description = "Call before the payment screen to show the user their discount summary. " +
                      "Returns the promo details if valid."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Code is valid and usable"),
        @ApiResponse(responseCode = "400", description = "Code not found",
                     content = @Content(schema = @Schema(example = "{\"error\":\"Promo code 'XYZ' is not recognised.\"}"))),
        @ApiResponse(responseCode = "409", description = "Code expired, inactive, or usage limit reached",
                     content = @Content(schema = @Schema(example = "{\"error\":\"Promo code 'EXPIRED24' expired on 2024-12-31.\"}")))
    })
    @GetMapping("/validate")
    public ResponseEntity<PromoCode> validate(
            @Parameter(description = "Promo code string, case-insensitive", example = "SAVE20")
            @RequestParam String code) {
        return ResponseEntity.ok(promoCodeUseCase.validate(code));
    }

    @Operation(summary = "List all promo codes", description = "Admin / back-office endpoint.")
    @GetMapping
    public ResponseEntity<List<PromoCode>> listAll() {
        return ResponseEntity.ok(promoCodeUseCase.getAll());
    }

    // ── Provisioning ──────────────────────────────────────────────────

    @Operation(
        summary     = "Provision a promo code to a customer or to all customers",
        description = "POST with `{ \"customerId\": 1 }` to grant to one customer. " +
                      "POST with `{ \"customerId\": null }` (or omit the field) to grant globally to ALL customers. " +
                      "Once provisioned, only eligible customers can apply the code at payment."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Assignment created"),
        @ApiResponse(responseCode = "400", description = "Promo code or customer not found"),
        @ApiResponse(responseCode = "409", description = "Assignment already exists")
    })
    @PostMapping("/{code}/assign")
    public ResponseEntity<PromoCodeAssignment> assign(
            @PathVariable String code,
            @RequestBody(required = false) PromoCodeAssignmentRequest request) {

        PromoCodeAssignment result = (request == null || request.getCustomerId() == null)
                ? assignmentUseCase.assignToAll(code)
                : assignmentUseCase.assignToCustomer(code, request.getCustomerId());

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(
        summary     = "List all assignments for a promo code",
        description = "Shows which customers (or ALL) this code has been provisioned to."
    )
    @GetMapping("/{code}/assignments")
    public ResponseEntity<List<PromoCodeAssignment>> getAssignments(@PathVariable String code) {
        return ResponseEntity.ok(assignmentUseCase.getAssignments(code));
    }

    // ── Usage tracking ────────────────────────────────────────────────

    @Operation(summary = "Usage log for a specific promo code")
    @GetMapping("/{code}/usage")
    public ResponseEntity<List<PromoCodeUsageLog>> getUsageByCode(@PathVariable String code) {
        return ResponseEntity.ok(assignmentUseCase.getUsageByPromoCode(code));
    }

    @Operation(summary = "Full usage log — all promo codes, all customers")
    @GetMapping("/usage")
    public ResponseEntity<List<PromoCodeUsageLog>> getAllUsage() {
        return ResponseEntity.ok(assignmentUseCase.getAllUsage());
    }

    @Operation(summary = "Usage log for a specific customer")
    @GetMapping("/usage/customer/{customerId}")
    public ResponseEntity<List<PromoCodeUsageLog>> getUsageByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(assignmentUseCase.getUsageByCustomer(customerId));
    }
}