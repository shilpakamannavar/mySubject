package com.study.management.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class GrantStudyCreationRequest {

    @NotNull(message = "memberId is required")
    private UUID memberId;

    public UUID getMemberId()              { return memberId; }
    public void setMemberId(UUID memberId) { this.memberId = memberId; }
}
