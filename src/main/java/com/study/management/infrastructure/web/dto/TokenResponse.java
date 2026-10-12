package com.study.management.infrastructure.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponse {
    private String accessToken;
    private String idToken;
    private String refreshToken;
    private Integer expiresIn;
    private String tokenType;
}
