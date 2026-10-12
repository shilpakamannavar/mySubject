package com.study.management.infrastructure.cognito;

import com.study.management.infrastructure.web.dto.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class PhoneAuthService {

    private final CognitoIdentityProviderClient cognitoClient;
    private final CognitoUserService cognitoUserService;
    private final String clientId;
    private final String clientSecret;
    private final String userPoolId;
    private final Map<String, String> roleToGroup;

    public PhoneAuthService(
            CognitoIdentityProviderClient cognitoClient,
            CognitoUserService cognitoUserService,
            @Value("${app.cognito.client-id}") String clientId,
            @Value("${app.cognito.client-secret:}") String clientSecret,
            @Value("${app.cognito.user-pool-id}") String userPoolId,
            @Value("${app.cognito.groups.primary-investigator}") String piGroup,
            @Value("${app.cognito.groups.co-investigator}")      String ciGroup,
            @Value("${app.cognito.groups.crc}")                  String crcGroup,
            @Value("${app.cognito.groups.cro}")                  String croGroup,
            @Value("${app.cognito.groups.hospital}")             String hospitalGroup,
            @Value("${app.cognito.groups.subject}")              String subjectGroup) {
        this.cognitoClient = cognitoClient;
        this.cognitoUserService = cognitoUserService;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.userPoolId = userPoolId;
        this.roleToGroup = Map.of(
            "primary-investigator", piGroup,
            "co-investigator",      ciGroup,
            "crc",                  crcGroup,
            "cro",                  croGroup,
            "hospital",             hospitalGroup,
            "subject",              subjectGroup
        );
    }

    // Appended server-side so a short numeric PIN satisfies Cognito's default password policy.
    // Users only ever type their PIN; this suffix is never exposed.
    private static final String PIN_SUFFIX = "StudyMgt@2025!";

    /**
     * Registers a new user with a phone number, PIN, and role.
     * Sets custom:role in Cognito so the idToken carries the role claim immediately after
     * OTP verification — no admin intervention needed for the user to log in.
     * Adds the user to the matching Cognito group so cognito:groups is also correct.
     */
    public void signUp(String phoneNumber, String name, String pin, String role) {
        // Only send standard attributes via the app client — custom attributes require
        // admin authority to be reliably written (app client write-permission may be restricted).
        SignUpRequest.Builder builder = SignUpRequest.builder()
                .clientId(clientId)
                .username(phoneNumber)
                .password(pin + PIN_SUFFIX)
                .userAttributes(
                        AttributeType.builder().name("phone_number").value(phoneNumber).build(),
                        AttributeType.builder().name("name").value(name).build()
                );

        if (!clientSecret.isBlank()) {
            builder.secretHash(computeSecretHash(phoneNumber));
        }

        try {
            cognitoClient.signUp(builder.build());
        } catch (UsernameExistsException e) {
            cognitoUserService.checkUserEnabled(phoneNumber);
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Phone number already registered. Please sign in.");
        } catch (InvalidParameterException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid phone number format. Use E.164 format, e.g. +919876543210");
        } catch (CognitoIdentityProviderException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Registration failed: " + e.awsErrorDetails().errorMessage());
        }

        // Set custom:role via admin API — bypasses app-client write restrictions entirely
        setRoleAttributes(phoneNumber, role);
    }

    /**
     * Sets custom:role on an existing Cognito user via admin API and adds them to the matching group.
     * Used during sign-up and by the admin assign-role endpoint for existing users.
     */
    public void setRoleAttributes(String phoneNumber, String role) {
        cognitoClient.adminUpdateUserAttributes(
                AdminUpdateUserAttributesRequest.builder()
                        .userPoolId(userPoolId)
                        .username(phoneNumber)
                        .userAttributes(AttributeType.builder()
                                .name("custom:role")
                                .value(role)
                                .build())
                        .build());

        String groupName = roleToGroup.get(role);
        if (groupName != null) {
            try {
                cognitoClient.adminAddUserToGroup(
                        AdminAddUserToGroupRequest.builder()
                                .userPoolId(userPoolId)
                                .username(phoneNumber)
                                .groupName(groupName)
                                .build());
            } catch (CognitoIdentityProviderException ignored) {
                // Non-fatal: custom:role is the authoritative source; group is supplementary
            }
        }
    }

    /**
     * Confirms sign-up using the 6-digit OTP sent by Cognito via SMS.
     * On success the user is CONFIRMED and can sign in.
     */
    public void confirmSignUp(String phoneNumber, String otp) {
        // Block if admin has explicitly disabled the account before OTP verification completes
        cognitoUserService.checkUserEnabled(phoneNumber);

        ConfirmSignUpRequest.Builder builder = ConfirmSignUpRequest.builder()
                .clientId(clientId)
                .username(phoneNumber)
                .confirmationCode(otp);

        if (!clientSecret.isBlank()) {
            builder.secretHash(computeSecretHash(phoneNumber));
        }

        try {
            cognitoClient.confirmSignUp(builder.build());
            cognitoClient.adminUpdateUserAttributes(
                    AdminUpdateUserAttributesRequest.builder()
                            .userPoolId(userPoolId)
                            .username(phoneNumber)
                            .userAttributes(AttributeType.builder()
                                    .name("custom:enabled")
                                    .value("yes")
                                    .build())
                            .build());
        } catch (CodeMismatchException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect OTP. Please try again.");
        } catch (ExpiredCodeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP has expired. Please request a new one.");
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Phone number not found.");
        } catch (CognitoIdentityProviderException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Verification failed: " + e.awsErrorDetails().errorMessage());
        }
    }

    /**
     * Authenticates a registered, confirmed user using their phone number and PIN.
     * Returns JWT tokens directly — no OTP round-trip needed for sign-in.
     */
    public TokenResponse signIn(String phoneNumber, String pin) {
        cognitoUserService.checkUserEnabled(phoneNumber);

        Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", phoneNumber);
        authParams.put("PASSWORD", pin + PIN_SUFFIX);
        if (!clientSecret.isBlank()) {
            authParams.put("SECRET_HASH", computeSecretHash(phoneNumber));
        }

        AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                .userPoolId(userPoolId)
                .clientId(clientId)
                .authParameters(authParams)
                .build();

        try {
            AdminInitiateAuthResponse authResponse = cognitoClient.adminInitiateAuth(authRequest);
            AuthenticationResultType result = authResponse.authenticationResult();
            return TokenResponse.builder()
                    .accessToken(result.accessToken())
                    .idToken(result.idToken())
                    .refreshToken(result.refreshToken())
                    .expiresIn(result.expiresIn())
                    .tokenType(result.tokenType())
                    .build();
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Phone number not registered. Please sign up first.");
        } catch (NotAuthorizedException e) {
            String msg = e.awsErrorDetails().errorMessage();
            if (msg != null && msg.contains("disabled")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Your account has been disabled. Please contact the administrator.");
            }
            if (msg != null && msg.contains("not confirmed")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Account not verified. Please complete sign-up OTP verification first.");
            }
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Incorrect PIN. Please try again.");
        } catch (CognitoIdentityProviderException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Authentication failed: " + e.awsErrorDetails().errorMessage());
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Computes the HMAC-SHA256 secret hash required when the Cognito app client
     * has a client secret configured.
     */
    private String computeSecretHash(String username) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(clientSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal((username + clientId).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to compute Cognito SECRET_HASH", e);
        }
    }
}
