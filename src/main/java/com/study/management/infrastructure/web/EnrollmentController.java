package com.study.management.infrastructure.web;

import com.study.management.domain.model.PatientEntity;
import com.study.management.domain.model.PatientVisit;
import com.study.management.domain.model.StudyEnrollment;
import com.study.management.domain.model.StudySiteAssignment;
import com.study.management.domain.port.in.EnrollmentUseCase;
import com.study.management.infrastructure.web.dto.EnrollPatientRequest;
import com.study.management.infrastructure.web.dto.EnrollmentResponse;
import com.study.management.infrastructure.web.dto.PagedResponse;
import com.study.management.infrastructure.web.dto.VisitItem;
import com.study.management.infrastructure.web.dto.WithdrawRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Enrollments", description = "Patient enrollment management")
@RestController
public class EnrollmentController {

    private final EnrollmentUseCase enrollmentUseCase;

    public EnrollmentController(EnrollmentUseCase enrollmentUseCase) {
        this.enrollmentUseCase = enrollmentUseCase;
    }

    @Operation(summary = "Enroll a patient in a study")
    @PostMapping("/api/v1/studies/{studyId}/enrollments")
    public ResponseEntity<EnrollmentResponse> enroll(@PathVariable UUID studyId,
                                                      @RequestBody EnrollPatientRequest req) {
        StudyEnrollment enrollment = new StudyEnrollment();
        PatientEntity patient = new PatientEntity();
        patient.setPatientId(req.getPatientId());
        enrollment.setPatient(patient);

        if (req.getStudySiteId() != null) {
            StudySiteAssignment site = new StudySiteAssignment();
            site.setStudySiteId(req.getStudySiteId());
            enrollment.setStudySite(site);
        }
        enrollment.setSubjectNumber(req.getSubjectNumber());
        enrollment.setScreeningDate(req.getScreeningDate());
        enrollment.setEnrollmentDate(req.getEnrollmentDate());
        enrollment.setInformedConsentDate(req.getInformedConsentDate());
        enrollment.setConsentVersion(req.getConsentVersion());
        enrollment.setConsentDocumentUrl(req.getConsentDocumentUrl());

        StudyEnrollment saved = enrollmentUseCase.enrollPatient(studyId, enrollment);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @Operation(summary = "List enrolled patients for a study")
    @GetMapping("/api/v1/studies/{studyId}/enrollments")
    public ResponseEntity<PagedResponse<EnrollmentResponse>> list(
            @PathVariable UUID studyId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<StudyEnrollment> enrollPage = enrollmentUseCase.listEnrollments(
                studyId, PageRequest.of(page - 1, size));
        List<EnrollmentResponse> items = enrollPage.getContent().stream()
                .map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(PagedResponse.of(items, page, size, enrollPage.getTotalElements()));
    }

    @Operation(summary = "Withdraw a patient from a study")
    @PatchMapping("/api/v1/studies/{studyId}/enrollments/{enrollmentId}/withdraw")
    public ResponseEntity<EnrollmentResponse> withdraw(@PathVariable UUID studyId,
                                                        @PathVariable UUID enrollmentId,
                                                        @RequestBody WithdrawRequest req) {
        StudyEnrollment updates = new StudyEnrollment();
        updates.setWithdrawalDate(req.getWithdrawalDate());
        updates.setWithdrawalReason(req.getWithdrawalReason());
        StudyEnrollment updated = enrollmentUseCase.withdrawPatient(studyId, enrollmentId, updates);
        return ResponseEntity.ok(toResponse(updated));
    }

    private EnrollmentResponse toResponse(StudyEnrollment e) {
        EnrollmentResponse r = new EnrollmentResponse();
        r.setEnrollmentId(e.getEnrollmentId());
        if (e.getStudy() != null) {
            r.setStudyId(e.getStudy().getStudyId());
            r.setStudyName(e.getStudy().getStudyName());
        }
        if (e.getPatient() != null) {
            r.setPatientId(e.getPatient().getPatientId());
            r.setPatientNumber(e.getPatient().getPatientNumber());
            r.setPatientName(e.getPatient().getFirstName() + " " + e.getPatient().getLastName());
        }
        if (e.getStudySite() != null) r.setStudySiteId(e.getStudySite().getStudySiteId());
        r.setSubjectNumber(e.getSubjectNumber());
        r.setScreeningDate(e.getScreeningDate());
        r.setEnrollmentDate(e.getEnrollmentDate());
        r.setRandomizationDate(e.getRandomizationDate());
        r.setRandomizationNumber(e.getRandomizationNumber());
        r.setCompletionDate(e.getCompletionDate());
        r.setWithdrawalDate(e.getWithdrawalDate());
        r.setWithdrawalReason(e.getWithdrawalReason());
        r.setInformedConsentDate(e.getInformedConsentDate());
        r.setConsentVersion(e.getConsentVersion());
        r.setEnrollmentStatus(e.getEnrollmentStatus());
        r.setCreatedAt(e.getCreatedAt());
        r.setUpdatedAt(e.getUpdatedAt());
        return r;
    }
}
