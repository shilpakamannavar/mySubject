package com.study.management.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserStatusRequest {
    @NotNull(message = "enabled flag is required")
    private Boolean enabled;
    /** Optional — if provided, only the named role's status is changed (custom:role_status).
     *  If absent, the global account is enabled/disabled (custom:enabled + native Cognito flag). */
    private String role;
}
