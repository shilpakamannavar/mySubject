package com.study.management.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class BulkUserStatusResult {
    private String username;
    private boolean success;
    private String message;
    /** custom:enabled value after the operation — "yes", "no", or null if user not found */
    private String customEnabled;
    /** Native Cognito enabled flag after the operation */
    private Boolean enabled;
    /** custom:role_status after the operation; null if not set */
    private Map<String, String> roleStatus;
}
