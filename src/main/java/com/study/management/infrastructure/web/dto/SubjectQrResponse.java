package com.study.management.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class SubjectQrResponse {
    private UUID subjectId;
    private String subjectNumber;
    /**
     * JSON payload to encode as the QR code image on the frontend.
     * Format: {"subjectId":"<uuid>","token":"<random-uuid>"}
     * When scanned, the subjectId is visible. Pass the full payload string to POST /subjects/activate.
     */
    private String qrToken;
    /** ISO-8601 expiry — frontend should warn the subject when this is near */
    private OffsetDateTime expiresAt;
}
