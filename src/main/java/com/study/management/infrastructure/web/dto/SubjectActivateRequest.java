package com.study.management.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SubjectActivateRequest {
    @NotBlank
    private String qrToken;
}
