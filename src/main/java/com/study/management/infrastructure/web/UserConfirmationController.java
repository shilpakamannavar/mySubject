package com.study.management.infrastructure.web;

import com.study.management.infrastructure.cognito.CognitoUserService;
import com.study.management.infrastructure.web.dto.UserConfirmationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "User confirmation status")
@RestController
@RequestMapping("/api/v1/auth")
public class UserConfirmationController {

    private final CognitoUserService cognitoUserService;

    public UserConfirmationController(CognitoUserService cognitoUserService) {
        this.cognitoUserService = cognitoUserService;
    }

    @Operation(
        summary = "Check if a user has completed OTP verification",
        description = "Returns the Cognito confirmation status and role for the given username (email or phone). " +
                      "Returns 'confirmed: true' only when UserStatus is CONFIRMED."
    )
    @GetMapping("/users/{username}/confirmation-status")
    public ResponseEntity<UserConfirmationResponse> getConfirmationStatus(
            @Parameter(description = "Cognito username — email or phone number used at registration")
            @PathVariable String username) {
        return ResponseEntity.ok(cognitoUserService.getConfirmationStatus(username));
    }
}
