package com.study.management.infrastructure.web;

import com.study.management.domain.model.PatientEntity;
import com.study.management.domain.model.PatientStudyLog;
import com.study.management.domain.model.PatientVisit;
import com.study.management.domain.model.StudyEnrollment;
import com.study.management.domain.model.StudySiteAssignment;
import com.study.management.domain.port.in.ClinicalStudyUseCase;
import com.study.management.domain.port.in.EnrollmentUseCase;
import com.study.management.domain.port.in.EnrollmentUseCase.EnrollmentEligibility;
import com.study.management.domain.port.in.PatientStudyLogUseCase;
import com.study.management.infrastructure.web.dto.EnrollPatientRequest;
import com.study.management.infrastructure.web.dto.EnrollmentResponse;
import com.study.management.infrastructure.web.dto.PagedResponse;
import com.study.management.infrastructure.web.dto.PatientStudyLogResponse;
import com.study.management.infrastructure.web.dto.SubjectAcceptEnrollmentRequest;
import com.study.management.infrastructure.web.dto.VisitItem;
import com.study.management.infrastructure.web.dto.WithdrawRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Enrollments", description = "Subject enrollment and study plan management")
@RestController
public class EnrollmentController {

    private final EnrollmentUseCase enrollmentUseCase;
    private final PatientStudyLogUseCase logUseCase;
    private final ClinicalStudyUseCase clinicalStudyUseCase;

    public EnrollmentController(EnrollmentUseCase enrollmentUseCase,
                                 PatientStudyLogUseCase logUseCase,
                                 ClinicalStudyUseCase clinicalStudyUseCase) {
        this.enrollmentUseCase = enrollmentUseCase;
        this.logUseCase = logUseCase;
        this.clinicalStudyUseCase = clinicalStudyUseCase;
    }

    @Operation(
        summary = "Check enrollment eligibility for a study",
        description = """
            Call this BEFORE showing the enroll dialog. Returns whether enrollment is currently
            allowed and, if not, the exact reason (study not ACTIVE, subscription not paid, etc.)

            **Response fields:**
            - `eligible` — true = enrollment is allowed right now
            - `reason` — human-readable explanation (show this to the user when eligible=false)
            - `studyStatus` — current status of the study
            - `subscriptionPaid` — whether the PI's subscription covers today
            - `subscriptionExpiry` — ISO date the current subscription expires (null if not paid)
            """
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CRC', 'CO_INVESTIGATOR')")
    @GetMapping("/api/v1/studies/{studyId}/enrollment-check")
    public ResponseEntity<EnrollmentUseCase.EnrollmentEligibility> checkEligibility(@PathVariable UUID studyId) {
        return ResponseEntity.ok(enrollmentUseCase.checkEnrollmentEligibility(studyId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CRC', 'CO_INVESTIGATOR')")
    @Operation(summary = "Enroll a patient in a study")
    @PostMapping("/api/v1/studies/{studyId}/enrollments")
    public ResponseEntity<EnrollmentResponse> enroll(@PathVariable UUID studyId,
                                                      @RequestBody EnrollPatientRequest req) {
        StudyEnrollment enrollment = new StudyEnrollment();
        enrollment.setSubjectId(req.getSubjectId());   // backend resolves → patient record
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
        EnrollmentResponse resp = toResponse(saved);
        // Include the auto-generated study plan for the subject
        List<PatientStudyLog> plan = logUseCase.getLogsByEnrollment(studyId, saved.getEnrollmentId());
        resp.setStudyPlan(plan.stream().map(this::toLogResponse).collect(Collectors.toList()));
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @Operation(
        summary = "List enrolled subjects for a study",
        description = "Returns all subjects enrolled in the given study. " +
                      "PRIMARY_INVESTIGATOR callers are restricted to studies they are assigned to — " +
                      "requesting a study they do not own returns 403."
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC')")
    @GetMapping("/api/v1/studies/{studyId}/enrollments")
    public ResponseEntity<PagedResponse<EnrollmentResponse>> list(
            @PathVariable UUID studyId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal Jwt jwt) {

        String callerRole  = jwt.getClaimAsString("custom:role");
        String callerPhone = jwt.getClaimAsString("phone_number");

        // PI can only view subjects in studies where they are the assigned PI
        if ("primary-investigator".equals(callerRole)) {
            boolean isAssignedToStudy = clinicalStudyUseCase
                    .getStudiesForPiByPhone(callerPhone)
                    .stream()
                    .anyMatch(s -> s.getStudyId().equals(studyId));
            if (!isAssignedToStudy) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "You are not the primary investigator for this study.");
            }
        }

        Page<StudyEnrollment> enrollPage = enrollmentUseCase.listEnrollments(
                studyId, PageRequest.of(page - 1, size));
        List<EnrollmentResponse> items = enrollPage.getContent().stream()
                .map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(PagedResponse.of(items, page, size, enrollPage.getTotalElements()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CRC', 'CO_INVESTIGATOR')")
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

    @Operation(
        summary = "Get subject's own enrollments (Subject only)",
        description = """
            Called immediately after subject login. Returns all studies this subject has been
            enrolled or invited into, looked up by the phone number in their JWT.

            Statuses the subject may see:
            - `CONSENT_PENDING` — PI created the enrollment; subject must accept before QR code generation
            - `ENROLLED` — subject has accepted; can now generate QR code and view schedule
            - `WITHDRAWN` / `COMPLETED` — terminal states
            """
    )
    @PreAuthorize("hasRole('SUBJECT')")
    @GetMapping("/api/v1/subjects/me/enrollments")
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments(@AuthenticationPrincipal Jwt jwt) {
        String phone = jwt.getClaimAsString("phone_number");
        if (phone == null) phone = jwt.getClaimAsString("email");
        List<StudyEnrollment> enrollments = enrollmentUseCase.getMyEnrollments(phone);
        return ResponseEntity.ok(enrollments.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @Operation(
        summary = "Subject accepts their enrollment (Subject only)",
        description = """
            Subject confirms informed consent. Transitions enrollment status from
            `CONSENT_PENDING` → `ENROLLED`.

            After a successful accept the subject can:
            1. Generate their QR code — `POST /api/v1/subjects/me/qr-code`
            2. View their full visit schedule — `GET /api/v1/studies/{studyId}/enrollments/{enrollmentId}/subject-study-plan`

            **Rules:**
            - Only the subject who owns this enrollment can accept it.
            - Accepts at any status except `WITHDRAWN` and `COMPLETED`.
            - `consentDate` defaults to today if omitted.
            """
    )
    @PreAuthorize("hasRole('SUBJECT')")
    @PostMapping("/api/v1/subjects/me/enrollments/{enrollmentId}/accept")
    public ResponseEntity<EnrollmentResponse> acceptEnrollment(
            @PathVariable UUID enrollmentId,
            @RequestBody(required = false) SubjectAcceptEnrollmentRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        String phone = jwt.getClaimAsString("phone_number");
        if (phone == null) phone = jwt.getClaimAsString("email");
        java.time.LocalDate consentDate = (req != null) ? req.getConsentDate() : null;
        String consentVersion = (req != null) ? req.getConsentVersion() : null;
        StudyEnrollment updated = enrollmentUseCase.acceptEnrollment(enrollmentId, phone, consentDate, consentVersion);
        return ResponseEntity.ok(toResponse(updated));
    }

    @Operation(summary = "Get the study plan for an enrolled subject (all scheduled visits with dates and instructions)")
    @PreAuthorize("hasAnyRole('ADMIN','PRIMARY_INVESTIGATOR','CO_INVESTIGATOR','CRC','SUBJECT')")
    @GetMapping("/api/v1/studies/{studyId}/enrollments/{enrollmentId}/subject-study-plan")
    public ResponseEntity<List<PatientStudyLogResponse>> getSubjectStudyPlan(
            @PathVariable UUID studyId,
            @PathVariable UUID enrollmentId) {
        List<PatientStudyLog> plan = logUseCase.getLogsByEnrollment(studyId, enrollmentId);
        return ResponseEntity.ok(plan.stream().map(this::toLogResponse).collect(Collectors.toList()));
    }

    private PatientStudyLogResponse toLogResponse(PatientStudyLog log) {
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
