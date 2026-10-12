package com.study.management.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class AdminUserListItem {
    private String username;
    private String givenName;
    private String familyName;
    private String role;
    private String userStatus;
    private boolean enabled;
    /** custom:enabled Cognito attribute — "yes", "no", or null (OTP not yet verified). */
    private String customEnabled;
    /** custom:role_status — per-role enabled flags, e.g. {"subject":"yes","co-investigator":"no"}; null if not set */
    private Map<String, String> roleStatus;
}
