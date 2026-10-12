package com.study.management.infrastructure.web;

import com.study.management.infrastructure.cognito.CognitoUserService;
import com.study.management.infrastructure.web.dto.AdminUserListItem;
import com.study.management.infrastructure.web.dto.AssignRoleRequest;
import com.study.management.infrastructure.web.dto.BulkUserStatusRequest;
import com.study.management.infrastructure.web.dto.BulkUserStatusResult;
import com.study.management.infrastructure.web.dto.UserConfirmationResponse;
import com.study.management.infrastructure.web.dto.UserStatusRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin", description = "Admin operations")
@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final CognitoUserService cognitoUserService;

    public AdminUserController(CognitoUserService cognitoUserService) {
        this.cognitoUserService = cognitoUserService;
    }

    @Operation(
        summary = "List all users (Admin only)",
        description = "Returns every user in the Cognito pool — primary investigators, co-investigators, " +
                      "CRCs, CROs, hospitals, and subjects — including role, confirmation status, " +
                      "native enabled flag, custom:enabled, and custom:role_status."
    )
    @GetMapping
    public ResponseEntity<List<AdminUserListItem>> listAllUsers() {
        return ResponseEntity.ok(cognitoUserService.listAllUsers());
    }

    @Operation(
        summary = "Get full status of a single user (Admin only)",
        description = "Returns confirmation status, native enabled flag, custom:enabled (App-level), " +
                      "and custom:role_status (per-role) for the given phone number."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User status returned"),
        @ApiResponse(responseCode = "404", description = "User not found in Cognito")
    })
    @GetMapping("/{username}/status")
    public ResponseEntity<UserConfirmationResponse> getUserStatus(
            @Parameter(description = "Phone number used at registration (E.164) — URL-encode '+' as %2B")
            @PathVariable String username) {
        return ResponseEntity.ok(cognitoUserService.getConfirmationStatus(username));
    }

    @Operation(
        summary = "Enable or disable a single user (Admin only)",
        description = """
            Set `enabled` to `true` or `false`.

            **Global (no `role` field):**
            - Sets native Cognito `Enabled` flag.
            - Sets `custom:enabled` to `"yes"` or `"no"` (App-level gate).
            - Blocks or unblocks the user from signing in for all roles.

            **Role-specific (`role` field present):**
            - Updates only `custom:role_status` for that role.
            - Global `custom:enabled` and native flag are untouched.
            - Only the specified role's access changes.

            Response always includes the live `customEnabled` value read back from Cognito
            after the update, so the frontend can confirm the change."""
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status updated — response contains updated custom:enabled"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "400", description = "enabled flag missing")
    })
    @PutMapping("/{username}/status")
    public ResponseEntity<UserConfirmationResponse> setUserStatus(
            @Parameter(description = "Phone number used at registration (E.164) — URL-encode '+' as %2B")
            @PathVariable String username,
            @Valid @RequestBody UserStatusRequest request) {

        String role = request.getRole();
        boolean enable = Boolean.TRUE.equals(request.getEnabled());
        UserConfirmationResponse updated;

        if (role != null && !role.isBlank()) {
            updated = enable
                    ? cognitoUserService.enableUserForRole(username, role)
                    : cognitoUserService.disableUserForRole(username, role);
        } else {
            updated = enable
                    ? cognitoUserService.enableUser(username)
                    : cognitoUserService.disableUser(username);
        }

        return ResponseEntity.ok(updated);
    }

    @Operation(
        summary = "Enable or disable multiple users in one call (Admin only)",
        description = """
            Same logic as the single-user endpoint but applied to a list of phone numbers.

            Each user is processed independently — a failure (e.g. user not found) is recorded
            in that user's result without aborting the rest of the batch.

            Response is a list with one entry per username, each containing:
            - `success` — whether the operation succeeded for that user
            - `customEnabled` — the live `custom:enabled` value after the update
            - `enabled` — the live native Cognito flag after the update
            - `roleStatus` — the live `custom:role_status` map after the update
            - `message` — human-readable outcome or error"""
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Batch processed — check per-user success flag in response"),
        @ApiResponse(responseCode = "400", description = "usernames list empty or enabled flag missing")
    })
    @PutMapping("/status/bulk")
    public ResponseEntity<List<BulkUserStatusResult>> bulkSetUserStatus(
            @Valid @RequestBody BulkUserStatusRequest request) {

        List<BulkUserStatusResult> results = cognitoUserService.bulkSetUserStatus(
                request.getUsernames(),
                Boolean.TRUE.equals(request.getEnabled()),
                request.getRole());

        return ResponseEntity.ok(results);
    }

    @Operation(
        summary = "Assign role to a user (Admin only)",
        description = """
            Sets `custom:role` on the Cognito user via admin API — bypasses app-client write restrictions.

            Use this for users who signed up before the role field was introduced, or
            whose role was not persisted correctly. The next time the user signs in their
            idToken will carry the new `custom:role` value and the 403 will be resolved.

            Valid role values: `primary-investigator`, `co-investigator`, `crc`, `cro`, `hospital`, `subject`"""
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Role assigned — user must sign in again to get a fresh idToken"),
        @ApiResponse(responseCode = "404", description = "User not found in Cognito"),
        @ApiResponse(responseCode = "400", description = "role field missing or invalid value")
    })
    @PutMapping("/{username}/role")
    public ResponseEntity<UserConfirmationResponse> assignRole(
            @Parameter(description = "Phone number used at registration (E.164) — URL-encode '+' as %2B")
            @PathVariable String username,
            @Valid @RequestBody AssignRoleRequest request) {
        return ResponseEntity.ok(cognitoUserService.assignRole(username, request.getRole()));
    }
}
