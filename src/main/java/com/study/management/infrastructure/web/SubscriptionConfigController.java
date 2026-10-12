package com.study.management.infrastructure.web;

import com.study.management.domain.model.SubscriptionConfig;
import com.study.management.domain.port.in.SubscriptionConfigUseCase;
import com.study.management.infrastructure.web.dto.SubscriptionConfigRequest;
import com.study.management.infrastructure.web.dto.SubscriptionConfigResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Subscription Config", description = "Admin-managed global subscription pricing")
@RestController
@RequestMapping("/api/v1/admin/subscription/config")
public class SubscriptionConfigController {

    private final SubscriptionConfigUseCase configUseCase;

    public SubscriptionConfigController(SubscriptionConfigUseCase configUseCase) {
        this.configUseCase = configUseCase;
    }

    @Operation(
        summary = "Get current subscription configuration",
        description = "Returns the active monthly billing rate. Accessible by ADMIN only."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<SubscriptionConfigResponse> getConfig() {
        return ResponseEntity.ok(SubscriptionConfigResponse.from(configUseCase.getConfig()));
    }

    @Operation(
        summary = "Update monthly subscription rate",
        description = "Admin sets the monthly billing amount charged to each study. Default is ₹299. " +
                      "The new rate applies from the next billing cycle onwards."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping
    public ResponseEntity<SubscriptionConfigResponse> updateRate(
            @Valid @RequestBody SubscriptionConfigRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        String updatedBy = jwt.getClaimAsString("phone_number");
        if (updatedBy == null) updatedBy = jwt.getClaimAsString("email");
        SubscriptionConfig updated = configUseCase.updateMonthlyRate(req.getMonthlyRate(), updatedBy);
        return ResponseEntity.ok(SubscriptionConfigResponse.from(updated));
    }
}
