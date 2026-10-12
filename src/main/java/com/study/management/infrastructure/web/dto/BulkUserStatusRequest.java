package com.study.management.infrastructure.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BulkUserStatusRequest {
    @NotEmpty(message = "usernames list must not be empty")
    private List<String> usernames;

    @NotNull(message = "enabled flag is required")
    private Boolean enabled;

    /** Optional — if provided, only this role's status is changed (custom:role_status).
     *  If absent, the global account is enabled/disabled (custom:enabled + native Cognito flag). */
    private String role;
}
