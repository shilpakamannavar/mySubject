package com.study.management.infrastructure.web;

import com.study.management.infrastructure.cognito.PhoneAuthService;
import com.study.management.infrastructure.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Phone Auth", description = "Phone number OTP — sign-up and sign-in without a password")
@RestController
@RequestMapping("/api/v1/auth/phone")
public class PhoneAuthController {

    private final PhoneAuthService phoneAuthService;

    public PhoneAuthController(PhoneAuthService phoneAuthService) {
        this.phoneAuthService = phoneAuthService;
    }

    /**
     * Step 1 of sign-up — register a phone number.
     * Cognito sends a 6-digit OTP to the phone via SMS.
     * Follow up with POST /signup/verify to confirm.
     */
    @Operation(
        summary = "Sign up with phone number",
        description = "Register with a phone number including country code (e.g. +919876543210 for India, " +
                      "+12025550123 for USA). Cognito sends a 6-digit OTP via SMS. " +
                      "Call /signup/verify next with that OTP."
    )
    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signUp(@Valid @RequestBody PhoneSignUpRequest request) {
        phoneAuthService.signUp(request.getPhoneNumber(), request.getName(), request.getPin(), request.getRole());
        String maskedPhone = maskPhone(request.getPhoneNumber());
        return ResponseEntity.ok(new MessageResponse(
                "OTP sent to " + maskedPhone + ". Enter it at /signup/verify to activate your account."));
    }

    /**
     * Step 2 of sign-up — confirm the OTP received via SMS.
     * On success the account is CONFIRMED and the user can sign in.
     */
    @Operation(
        summary = "Verify sign-up OTP",
        description = "Submit the 6-digit OTP received via SMS to complete account registration. " +
                      "After this step the account is active and the user can call /signin."
    )
    @PostMapping("/signup/verify")
    public ResponseEntity<MessageResponse> verifySignUp(@Valid @RequestBody PhoneVerifyRequest request) {
        phoneAuthService.confirmSignUp(request.getPhoneNumber(), request.getOtp());
        return ResponseEntity.ok(new MessageResponse("Account verified. You can now sign in."));
    }

    /**
     * Sign in with phone number and PIN.
     * Returns JWT tokens immediately — no OTP round-trip needed.
     * Use the idToken as:  Authorization: Bearer <idToken>
     */
    @Operation(
        summary = "Sign in with phone number and PIN",
        description = "Authenticate using your registered phone number and the PIN you set during sign-up. " +
                      "Returns idToken, accessToken, and refreshToken immediately. " +
                      "Send the idToken as 'Authorization: Bearer <idToken>' on all subsequent API calls."
    )
    @PostMapping("/signin")
    public ResponseEntity<TokenResponse> signIn(@Valid @RequestBody PhoneSignInRequest request) {
        TokenResponse tokens = phoneAuthService.signIn(request.getPhoneNumber(), request.getPin());
        return ResponseEntity.ok(tokens);
    }

    /** Masks all but the last 4 digits: +919876543210 → +91XXXXXX3210 */
    private String maskPhone(String phone) {
        if (phone.length() <= 4) return phone;
        String prefix = phone.substring(0, phone.length() - 4).replaceAll("\\d", "X");
        return prefix + phone.substring(phone.length() - 4);
    }
}
