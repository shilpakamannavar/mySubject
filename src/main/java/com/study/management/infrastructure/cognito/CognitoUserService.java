package com.study.management.infrastructure.cognito;

import com.study.management.infrastructure.web.dto.AdminUserListItem;
import com.study.management.infrastructure.web.dto.BulkUserStatusResult;
import com.study.management.infrastructure.web.dto.UserConfirmationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CognitoUserService {

    private final CognitoIdentityProviderClient cognitoClient;
    private final String userPoolId;

    public CognitoUserService(
            CognitoIdentityProviderClient cognitoClient,
            @Value("${app.cognito.user-pool-id}") String userPoolId) {
        this.cognitoClient = cognitoClient;
        this.userPoolId    = userPoolId;
    }

    /** Used by the dedicated endpoint — throws 404 if the user is not in Cognito. */
    public UserConfirmationResponse getConfirmationStatus(String username) {
        try {
            return fetchFromCognito(username);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User not found: " + username);
        }
    }

    /** Used by list/get enrichment — returns NOT_REGISTERED instead of throwing. */
    public UserConfirmationResponse getConfirmationStatusSafe(String username) {
        if (username == null || username.isBlank()) {
            return new UserConfirmationResponse(null, null, false, "NOT_REGISTERED", true, null, null);
        }
        try {
            return fetchFromCognito(username);
        } catch (UserNotFoundException e) {
            return new UserConfirmationResponse(username, null, false, "NOT_REGISTERED", true, null, null);
        } catch (Exception e) {
            return new UserConfirmationResponse(username, null, false, "COGNITO_ERROR", true, null, null);
        }
    }

    /**
     * Throws 403 if the Cognito user is disabled — checks three layers:
     *  1. Native Cognito account enabled flag
     *  2. custom:enabled attribute (global app-level gate, set after OTP verification)
     *  3. custom:role_status entry for the user's assigned role (role-level gate)
     */
    public void checkUserEnabled(String username) {
        try {
            AdminGetUserResponse user = cognitoClient.adminGetUser(
                    AdminGetUserRequest.builder()
                            .userPoolId(userPoolId)
                            .username(username)
                            .build());

            // 1. Native Cognito enabled flag
            if (Boolean.FALSE.equals(user.enabled())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Your account has been disabled. Please contact the administrator.");
            }

            // 2. Global custom:enabled attribute
            String customEnabled = user.userAttributes().stream()
                    .filter(a -> "custom:enabled".equals(a.name()))
                    .findFirst()
                    .map(AttributeType::value)
                    .orElse(null);
            if (customEnabled != null && !"yes".equals(customEnabled)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Your account has been disabled. Please contact the administrator.");
            }

            // 3. Role-specific custom:role_status — only blocks if the user's role is explicitly "no"
            String role = user.userAttributes().stream()
                    .filter(a -> "custom:role".equals(a.name()))
                    .findFirst()
                    .map(AttributeType::value)
                    .orElse(null);
            String roleStatusRaw = user.userAttributes().stream()
                    .filter(a -> "custom:role_status".equals(a.name()))
                    .findFirst()
                    .map(AttributeType::value)
                    .orElse(null);
            if (role != null && roleStatusRaw != null) {
                String roleEnabled = parseRoleStatus(roleStatusRaw).get(role);
                if ("no".equals(roleEnabled)) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                            "Your account has been disabled for the '" + role + "' role. Please contact the administrator.");
                }
            }

        } catch (UserNotFoundException e) {
            // Not yet registered — callers handle this
        }
    }

    /** Throws 403 if the target user's custom:role is "subject" — admins may not disable subjects. */
    private void rejectIfSubject(String username) {
        AdminGetUserResponse user = cognitoClient.adminGetUser(
                AdminGetUserRequest.builder().userPoolId(userPoolId).username(username).build());
        String role = user.userAttributes().stream()
                .filter(a -> "custom:role".equals(a.name()))
                .findFirst().map(AttributeType::value).orElse(null);
        if ("subject".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Admin is not permitted to enable or disable subject accounts.");
        }
    }

    /**
     * Sets custom:role on any existing Cognito user — admin only.
     * Used to assign or correct the role for users who signed up before role was captured,
     * or whose role was not persisted due to app-client attribute write restrictions.
     * Throws 404 if the user does not exist.
     */
    public UserConfirmationResponse assignRole(String username, String role) {
        try {
            cognitoClient.adminUpdateUserAttributes(
                    AdminUpdateUserAttributesRequest.builder()
                            .userPoolId(userPoolId)
                            .username(username)
                            .userAttributes(AttributeType.builder()
                                    .name("custom:role")
                                    .value(role)
                                    .build())
                            .build());
            return fetchFromCognito(username);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User not found: " + username);
        } catch (CognitoIdentityProviderException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Failed to assign role: " + e.awsErrorDetails().errorMessage());
        }
    }

    /**
     * Globally disables a Cognito user (admin only).
     * Sets native Cognito Enabled=false and custom:enabled="no".
     * Returns the full updated status so callers can confirm the change.
     * Throws 404 if the user doesn't exist.
     */
    public UserConfirmationResponse disableUser(String username) {
        try {
            rejectIfSubject(username);
            cognitoClient.adminDisableUser(
                    AdminDisableUserRequest.builder()
                            .userPoolId(userPoolId)
                            .username(username)
                            .build());
            cognitoClient.adminUpdateUserAttributes(
                    AdminUpdateUserAttributesRequest.builder()
                            .userPoolId(userPoolId)
                            .username(username)
                            .userAttributes(AttributeType.builder()
                                    .name("custom:enabled")
                                    .value("no")
                                    .build())
                            .build());
            return fetchFromCognito(username);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User not found: " + username);
        }
    }

    /**
     * Globally enables a Cognito user (admin only).
     * Sets native Cognito Enabled=true and custom:enabled="yes".
     * Returns the full updated status so callers can confirm the change.
     * Throws 404 if not found.
     */
    public UserConfirmationResponse enableUser(String username) {
        try {
            rejectIfSubject(username);
            cognitoClient.adminEnableUser(
                    AdminEnableUserRequest.builder()
                            .userPoolId(userPoolId)
                            .username(username)
                            .build());
            cognitoClient.adminUpdateUserAttributes(
                    AdminUpdateUserAttributesRequest.builder()
                            .userPoolId(userPoolId)
                            .username(username)
                            .userAttributes(AttributeType.builder()
                                    .name("custom:enabled")
                                    .value("yes")
                                    .build())
                            .build());
            return fetchFromCognito(username);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User not found: " + username);
        }
    }

    /**
     * Disables a user for a specific role only (admin only).
     * Does not touch the native Cognito enabled flag or custom:enabled.
     * Returns the full updated status after the change.
     */
    public UserConfirmationResponse disableUserForRole(String username, String role) {
        rejectIfSubject(username);
        updateRoleStatus(username, role, "no");
        return fetchFromCognito(username);
    }

    /**
     * Enables a user for a specific role (admin only).
     * Returns the full updated status after the change.
     */
    public UserConfirmationResponse enableUserForRole(String username, String role) {
        rejectIfSubject(username);
        updateRoleStatus(username, role, "yes");
        return fetchFromCognito(username);
    }

    /**
     * Enables or disables a list of users in one call (admin only).
     * Each user is processed independently — a failure for one does not stop the rest.
     * Returns a per-user result including the updated custom:enabled status.
     */
    public List<BulkUserStatusResult> bulkSetUserStatus(List<String> usernames, boolean enable, String role) {
        List<BulkUserStatusResult> results = new ArrayList<>();
        for (String username : usernames) {
            try {
                UserConfirmationResponse updated;
                if (role != null && !role.isBlank()) {
                    updated = enable ? enableUserForRole(username, role) : disableUserForRole(username, role);
                } else {
                    updated = enable ? enableUser(username) : disableUser(username);
                }
                results.add(new BulkUserStatusResult(
                        username, true,
                        enable ? "Enabled successfully" : "Disabled successfully",
                        updated.getCustomEnabled(),
                        updated.isEnabled(),
                        updated.getRoleStatus()));
            } catch (ResponseStatusException e) {
                results.add(new BulkUserStatusResult(
                        username, false, e.getReason(), null, null, null));
            } catch (Exception e) {
                results.add(new BulkUserStatusResult(
                        username, false, "Unexpected error: " + e.getMessage(), null, null, null));
            }
        }
        return results;
    }

    /**
     * Lists all users in the pool with their role, enabled status, and per-role status.
     * Cognito paginates at 60 users per page; this method fetches all pages.
     */
    public List<AdminUserListItem> listAllUsers() {
        List<AdminUserListItem> users = new ArrayList<>();
        String paginationToken = null;

        do {
            ListUsersRequest.Builder req = ListUsersRequest.builder()
                    .userPoolId(userPoolId)
                    .limit(60);
            if (paginationToken != null) {
                req.paginationToken(paginationToken);
            }
            ListUsersResponse page = cognitoClient.listUsers(req.build());
            for (UserType u : page.users()) {
                String role = u.attributes().stream()
                        .filter(a -> "custom:role".equals(a.name()))
                        .findFirst().map(AttributeType::value).orElse(null);
                String givenName = u.attributes().stream()
                        .filter(a -> "given_name".equals(a.name()))
                        .findFirst().map(AttributeType::value).orElse(null);
                String familyName = u.attributes().stream()
                        .filter(a -> "family_name".equals(a.name()))
                        .findFirst().map(AttributeType::value).orElse(null);
                String customEnabled = u.attributes().stream()
                        .filter(a -> "custom:enabled".equals(a.name()))
                        .findFirst().map(AttributeType::value).orElse(null);
                String roleStatusRaw = u.attributes().stream()
                        .filter(a -> "custom:role_status".equals(a.name()))
                        .findFirst().map(AttributeType::value).orElse(null);
                if ("subject".equals(role)) continue;
                Map<String, String> roleStatus = parseRoleStatus(roleStatusRaw);
                users.add(new AdminUserListItem(
                        u.username(),
                        givenName,
                        familyName,
                        role,
                        u.userStatusAsString(),
                        Boolean.TRUE.equals(u.enabled()),
                        customEnabled,
                        roleStatus.isEmpty() ? null : roleStatus));
            }
            paginationToken = page.paginationToken();
        } while (paginationToken != null);

        return users;
    }

    // ── private ──────────────────────────────────────────────────────────────

    private UserConfirmationResponse fetchFromCognito(String username) {
        AdminGetUserResponse response = cognitoClient.adminGetUser(
                AdminGetUserRequest.builder()
                        .userPoolId(userPoolId)
                        .username(username)
                        .build());

        String userStatus = response.userStatusAsString();
        String role = response.userAttributes().stream()
                .filter(attr -> "custom:role".equals(attr.name()))
                .findFirst()
                .map(AttributeType::value)
                .orElse(null);
        String customEnabled = response.userAttributes().stream()
                .filter(attr -> "custom:enabled".equals(attr.name()))
                .findFirst()
                .map(AttributeType::value)
                .orElse(null);
        String roleStatusRaw = response.userAttributes().stream()
                .filter(attr -> "custom:role_status".equals(attr.name()))
                .findFirst()
                .map(AttributeType::value)
                .orElse(null);
        Map<String, String> roleStatus = parseRoleStatus(roleStatusRaw);

        return new UserConfirmationResponse(
                username,
                role,
                "CONFIRMED".equals(userStatus),
                userStatus,
                Boolean.TRUE.equals(response.enabled()),
                customEnabled,
                roleStatus.isEmpty() ? null : roleStatus);
    }

    /** Reads, merges, and writes back a single role entry in custom:role_status. */
    private void updateRoleStatus(String username, String role, String status) {
        try {
            AdminGetUserResponse user = cognitoClient.adminGetUser(
                    AdminGetUserRequest.builder()
                            .userPoolId(userPoolId)
                            .username(username)
                            .build());
            String existing = user.userAttributes().stream()
                    .filter(a -> "custom:role_status".equals(a.name()))
                    .findFirst()
                    .map(AttributeType::value)
                    .orElse(null);
            Map<String, String> map = parseRoleStatus(existing);
            map.put(role, status);
            cognitoClient.adminUpdateUserAttributes(
                    AdminUpdateUserAttributesRequest.builder()
                            .userPoolId(userPoolId)
                            .username(username)
                            .userAttributes(AttributeType.builder()
                                    .name("custom:role_status")
                                    .value(encodeRoleStatus(map))
                                    .build())
                            .build());
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + username);
        }
    }

    /** Parses "subject:yes,co-investigator:no" → Map{subject=yes, co-investigator=no} */
    private Map<String, String> parseRoleStatus(String raw) {
        Map<String, String> map = new LinkedHashMap<>();
        if (raw == null || raw.isBlank()) return map;
        for (String entry : raw.split(",")) {
            String[] parts = entry.split(":", 2);
            if (parts.length == 2) map.put(parts[0].trim(), parts[1].trim());
        }
        return map;
    }

    /** Encodes Map{subject=yes, co-investigator=no} → "subject:yes,co-investigator:no" */
    private String encodeRoleStatus(Map<String, String> map) {
        return map.entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .collect(Collectors.joining(","));
    }
}
