package com.study.management.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AssignRoleRequest {

    @NotBlank(message = "role is required")
    @Pattern(
        regexp = "primary-investigator|co-investigator|crc|cro|hospital|subject",
        message = "role must be one of: primary-investigator, co-investigator, crc, cro, hospital, subject"
    )
    private String role;
}
