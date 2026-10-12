package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.ActivationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class SubjectActivationStatusResponse {
    private UUID subjectId;
    private String subjectNumber;
    private String firstName;
    private String lastName;
    private ActivationStatus activationStatus;
    private OffsetDateTime activatedAt;
    private String activatedBy;
    private OffsetDateTime deactivatedAt;
    private String deactivatedBy;
}
