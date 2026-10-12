package com.study.management.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PhoneVerifyRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^\\+[1-9]\\d{6,14}$",
        message = "Phone number must be in E.164 format (e.g. +919876543210)"
    )
    private String phoneNumber;

    @NotBlank(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be exactly 6 digits")
    @Pattern(regexp = "\\d{6}", message = "OTP must contain only digits")
    private String otp;
}
