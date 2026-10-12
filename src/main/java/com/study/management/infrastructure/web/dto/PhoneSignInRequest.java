package com.study.management.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PhoneSignInRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^\\+[1-9]\\d{6,14}$",
        message = "Phone number must be in E.164 format — country code followed by number (e.g. +919876543210 or +12025550123)"
    )
    private String phoneNumber;

    @NotBlank(message = "PIN is required")
    @Size(min = 4, max = 6, message = "PIN must be 4 to 6 digits")
    @Pattern(regexp = "\\d{4,6}", message = "PIN must contain only digits")
    private String pin;
}
