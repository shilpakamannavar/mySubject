package com.study.management.infrastructure.web;

import com.study.management.domain.model.PatientStudyLog;
import com.study.management.domain.port.in.PatientStudyLogUseCase;
import com.study.management.infrastructure.web.dto.PatientStudyLogResponse;
import com.study.management.infrastructure.web.dto.RecordActualVisitRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Patient Study Log", description = "Auto-generated per-visit CRC tracking grid")
@RestController
@RequestMapping("/api/v1/studies/{studyId}")
public class PatientStudyLogController {

    private final PatientStudyLogUseCase logUseCase;

    public PatientStudyLogController(PatientStudyLogUseCase logUseCase) {
        this.logUseCase = logUseCase;
    }

    @Operation(summary = "Get all patient study logs for a study")
    @PreAuthorize("hasAnyRole('ADMIN','PRIMARY_INVESTIGATOR','CO_INVESTIGATOR','CRC')")
    @GetMapping("/study-logs")
    public ResponseEntity<List<PatientStudyLogResponse>> getLogsByStudy(@PathVariable UUID studyId) {
        List<PatientStudyLogResponse> result = logUseCase.getLogsByStudy(studyId)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get patient study logs for a specific enrollment")
    @PreAuthorize("hasAnyRole('ADMIN','PRIMARY_INVESTIGATOR','CO_INVESTIGATOR','CRC','SUBJECT')")
    @GetMapping("/enrollments/{enrollmentId}/study-logs")
    public ResponseEntity<List<PatientStudyLogResponse>> getLogsByEnrollment(
            @PathVariable UUID studyId,
            @PathVariable UUID enrollmentId) {
        List<PatientStudyLogResponse> result = logUseCase.getLogsByEnrollment(studyId, enrollmentId)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Record actual visit date and auto-resolve visit status")
    @PreAuthorize("hasAnyRole('ADMIN','PRIMARY_INVESTIGATOR','CRC')")
    @PatchMapping("/study-logs/{logId}/actual-visit")
    public ResponseEntity<PatientStudyLogResponse> recordActualVisit(
            @PathVariable UUID studyId,
            @PathVariable String logId,
            @RequestBody RecordActualVisitRequest req) {
        PatientStudyLog updated = logUseCase.recordActualVisit(logId, req.getActualDate());
        return ResponseEntity.ok(toResponse(updated));
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private PatientStudyLogResponse toResponse(PatientStudyLog log) {
        PatientStudyLogResponse r = new PatientStudyLogResponse();
        r.setLogId(log.getLogId());
        r.setPatientNumber(log.getPatientNumber());
        r.setStudyId(log.getStudy() != null ? log.getStudy().getStudyId() : null);
        r.setEnrollmentId(log.getEnrollment() != null ? log.getEnrollment().getEnrollmentId() : null);
        r.setVisitNumber(log.getVisitNumber());
        r.setVisitName(log.getPlanVisit() != null ? log.getPlanVisit().getVisitName() : null);
        r.setVisitType(log.getVisitType());
        r.setTargetScheduledDate(log.getTargetScheduledDate());
        r.setWindowOpenDate(log.getWindowOpenDate());
        r.setWindowCloseDate(log.getWindowCloseDate());
        r.setActualVisitDate(log.getActualVisitDate());
        r.setPatientInstruction(log.getVisitSpecificInstructions());
        r.setVisitSpecificInstructions(log.getVisitSpecificInstructions());
        r.setVisitStatus(log.getVisitStatus());
        r.setReadOnly(log.isReadOnly());
        r.setCreatedAt(log.getCreatedAt());
        r.setUpdatedAt(log.getUpdatedAt());
        return r;
    }
}
