package com.study.management.infrastructure.web;

import com.study.management.domain.model.StudySubscription;
import com.study.management.domain.port.in.StudySubscriptionUseCase;
import com.study.management.infrastructure.web.dto.PaySubscriptionRequest;
import com.study.management.infrastructure.web.dto.StudySubscriptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "PI Subscriptions", description = "Monthly subscription billing per Primary Investigator — one payment covers all their studies")
@RestController
@RequestMapping("/api/v1/investigators/{piId}/subscription")
public class StudySubscriptionController {

    private final StudySubscriptionUseCase subscriptionUseCase;

    public StudySubscriptionController(StudySubscriptionUseCase subscriptionUseCase) {
        this.subscriptionUseCase = subscriptionUseCase;
    }

    @Operation(
        summary = "Get PI's current month subscription status",
        description = """
            Returns the subscription record for the PI for the current billing month.
            A single PAID subscription here unlocks subject enrollment across **all studies** owned by this PI.

            If no payment has been made yet, returns a PENDING record showing the amount due.
            """
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR')")
    @GetMapping("/current")
    public ResponseEntity<StudySubscriptionResponse> getCurrentMonth(@PathVariable UUID piId) {
        StudySubscription sub = subscriptionUseCase.getPiCurrentMonthSubscription(piId);
        return ResponseEntity.ok(StudySubscriptionResponse.from(sub));
    }

    @Operation(
        summary = "Pay a PI subscription — specify duration and payment date",
        description = """
            SMO (ADMIN) or the Primary Investigator themselves pays a subscription.
            One payment covers **all studies** created by this PI for the full duration.

            **Request fields:**
            - `paymentDate` — date of payment; becomes the subscription start date. Defaults to today.
            - `durationMonths` — how many months to cover (1–36). Defaults to 1.
            - `paymentReference` — optional transaction ID; auto-generated if omitted.

            **Total charge** = configured monthly rate × durationMonths.

            **Rules:**
            - Only `ADMIN` (SMO) or `PRIMARY_INVESTIGATOR` can call this.
            - Attempting to pay an already-PAID period that starts in the same month returns `400`.
            """
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR')")
    @PostMapping("/pay")
    public ResponseEntity<StudySubscriptionResponse> pay(
            @PathVariable UUID piId,
            @RequestBody(required = false) PaySubscriptionRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        String paidBy = jwt.getClaimAsString("phone_number");
        if (paidBy == null) paidBy = jwt.getClaimAsString("email");

        String ref = (req != null) ? req.getPaymentReference() : null;
        java.time.LocalDate paymentDate = (req != null) ? req.getPaymentDate() : null;
        int durationMonths = (req != null && req.getDurationMonths() != null) ? req.getDurationMonths() : 1;

        StudySubscription paid = subscriptionUseCase.payPiCurrentMonthSubscription(
                piId, paidBy, ref, paymentDate, durationMonths);
        return ResponseEntity.ok(StudySubscriptionResponse.from(paid));
    }

    @Operation(
        summary = "Get subscription payment history for a PI",
        description = "Returns all monthly subscription records for this PI, newest first."
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR')")
    @GetMapping("/history")
    public ResponseEntity<List<StudySubscriptionResponse>> history(@PathVariable UUID piId) {
        List<StudySubscription> history = subscriptionUseCase.getPiSubscriptionHistory(piId);
        return ResponseEntity.ok(history.stream()
                .map(StudySubscriptionResponse::from)
                .collect(Collectors.toList()));
    }
}
