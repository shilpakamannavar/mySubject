package com.study.management.infrastructure.web;

import com.study.management.domain.model.Subject;
import com.study.management.domain.port.in.SubjectUseCase;
import com.study.management.domain.port.out.PatientPort;
import com.study.management.infrastructure.cognito.CognitoSyncService;
import com.study.management.infrastructure.cognito.CognitoUserService;
import com.study.management.infrastructure.web.dto.CognitoSyncReport;
import com.study.management.infrastructure.web.dto.CreateSubjectRequest;
import com.study.management.infrastructure.web.dto.SubjectActivateRequest;
import com.study.management.infrastructure.web.dto.SubjectActivationStatusResponse;
import com.study.management.infrastructure.web.dto.SubjectQrResponse;
import com.study.management.infrastructure.web.dto.SubjectResponse;
import com.study.management.infrastructure.web.dto.UserConfirmationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Subjects", description = "Study subject management")
@RestController
@RequestMapping("/api/v1/subjects")
public class SubjectController {

    private final SubjectUseCase subjectUseCase;
    private final CognitoUserService cognitoUserService;
    private final CognitoSyncService cognitoSyncService;
    private final PatientPort patientPort;

    public SubjectController(SubjectUseCase subjectUseCase,
                             CognitoUserService cognitoUserService,
                             CognitoSyncService cognitoSyncService,
                             PatientPort patientPort) {
        this.subjectUseCase = subjectUseCase;
        this.cognitoUserService = cognitoUserService;
        this.cognitoSyncService = cognitoSyncService;
        this.patientPort = patientPort;
    }

    @Operation(summary = "List all subjects",
               description = "Silently syncs the Cognito subject group into the DB before returning. Admin is excluded — subject data is only accessible to clinical staff.")
    @PreAuthorize("hasAnyRole('PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC', 'CRO', 'HOSPITAL')")
    @GetMapping
    public ResponseEntity<List<SubjectResponse>> listAll() {
        cognitoSyncService.syncGroupIntoDb("subject");
        return ResponseEntity.ok(subjectUseCase.listAll().stream()
                .map(this::toResponse).collect(Collectors.toList()));
    }

    @Operation(summary = "Sync subjects from Cognito (with report)",
               description = "Fetches every member of the subject Cognito group, creates missing DB records, " +
                             "and returns a per-user CREATED / SKIPPED / ERROR report.")
    @PostMapping("/sync")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CognitoSyncReport> syncFromCognito() {
        return ResponseEntity.ok(cognitoSyncService.syncSingleRole("subject"));
    }

    @Operation(summary = "Create a new subject")
    @PostMapping
    public ResponseEntity<SubjectResponse> create(@RequestBody CreateSubjectRequest req) {
        Subject subject = new Subject();
        subject.setFirstName(req.getFirstName());
        subject.setLastName(req.getLastName());
        subject.setInitials(req.getInitials());
        subject.setDateOfBirth(req.getDateOfBirth());
        subject.setGender(req.getGender());
        subject.setEmail(req.getEmail());
        subject.setPhone(req.getPhone());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(subjectUseCase.create(subject)));
    }

    @Operation(summary = "Get subject by ID")
    @PreAuthorize("hasAnyRole('PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC', 'CRO', 'HOSPITAL', 'SUBJECT')")
    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(subjectUseCase.getById(id)));
    }

    @Operation(
        summary = "Link subject to their patient record",
        description = """
            CRC or Admin calls this once after creating both the Cognito subject account and
            the patient record. Sets `patient_id` directly on the subject row so the
            `patientId` field is always returned reliably — without depending on phone-string matching.

            After this call, `GET /api/v1/subjects/{id}` and `GET /api/v1/subjects/me/enrollments`
            will always return the correct `patientId`.
            """
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC')")
    @PatchMapping("/{id}/link-patient/{patientId}")
    public ResponseEntity<SubjectResponse> linkToPatient(
            @PathVariable UUID id,
            @PathVariable UUID patientId) {
        Subject subject = subjectUseCase.linkToPatient(id, patientId);
        return ResponseEntity.ok(toResponse(subject));
    }

    @Operation(
        summary = "Generate QR code token (Subject only)",
        description = """
            The authenticated subject generates (or refreshes) their personal QR token.
            Token is valid for **24 hours** and is **single-use** — it is cleared as soon as someone scans and activates it.

            **Rules:**
            - Only the `subject` role can call this endpoint.
            - The token is tied to the subject's phone number from their Cognito JWT.
            - Calling this again before the previous token expires will replace it with a fresh 24-hour token.
            - The frontend should render `qrToken` as a QR code image for the subject to display."""
    )
    @PreAuthorize("hasRole('SUBJECT')")
    @PostMapping("/me/qr-code")
    public ResponseEntity<SubjectQrResponse> generateQrCode(@AuthenticationPrincipal Jwt jwt) {
        String phone = jwt.getClaimAsString("phone_number");
        Subject subject = subjectUseCase.generateQrToken(phone);
        return ResponseEntity.ok(new SubjectQrResponse(
                subject.getSubjectId(),
                subject.getSubjectNumber(),
                subject.getQrToken(),
                subject.getQrTokenExpiresAt()));
    }

    @Operation(
        summary = "Activate a subject by scanning their QR code",
        description = """
            Admin, Primary Investigator, Co-Investigator, or CRC scans the subject's QR code and submits the token here.

            **Rules:**
            - Only `admin`, `primary-investigator`, `co-investigator`, and `crc` can activate.
            - The QR token must not be expired (valid for 24 hours after generation).
            - The token is **one-time use**: it is cleared from the database upon successful activation.
            - A subject must be activated before any visits can be recorded for them."""
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC')")
    @PostMapping("/activate")
    public ResponseEntity<SubjectActivationStatusResponse> activate(
            @Valid @RequestBody SubjectActivateRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        String activatedByPhone = jwt.getClaimAsString("phone_number");
        Subject subject = subjectUseCase.activateByQrToken(req.getQrToken(), activatedByPhone);
        return ResponseEntity.ok(toActivationResponse(subject));
    }

    @Operation(
        summary = "Deactivate a subject",
        description = """
            Admin, Primary Investigator, Co-Investigator, or CRC deactivates the subject with the given ID.

            **Rules:**
            - Only `admin`, `primary-investigator`, `co-investigator`, and `crc` can deactivate.
            - A deactivated subject's visits cannot be recorded until they are re-activated via QR code scan."""
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<SubjectActivationStatusResponse> deactivate(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        String deactivatedByPhone = jwt.getClaimAsString("phone_number");
        Subject subject = subjectUseCase.deactivate(id, deactivatedByPhone);
        return ResponseEntity.ok(toActivationResponse(subject));
    }

    @Operation(
        summary = "Get subject activation status",
        description = "Returns the current activation status, who activated/deactivated the subject, and when."
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC', 'SUBJECT')")
    @GetMapping("/{id}/activation-status")
    public ResponseEntity<SubjectActivationStatusResponse> getActivationStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(toActivationResponse(subjectUseCase.getById(id)));
    }

    private SubjectActivationStatusResponse toActivationResponse(Subject s) {
        return new SubjectActivationStatusResponse(
                s.getSubjectId(),
                s.getSubjectNumber(),
                s.getFirstName(),
                s.getLastName(),
                s.getActivationStatus(),
                s.getActivatedAt(),
                s.getActivatedBy(),
                s.getDeactivatedAt(),
                s.getDeactivatedBy());
    }

    private SubjectResponse toResponse(Subject s) {
        SubjectResponse r = new SubjectResponse();
        r.setSubjectId(s.getSubjectId());
        r.setSubjectNumber(s.getSubjectNumber());

        // Prefer the explicit FK; fall back to phone then email matching for older records.
        if (s.getPatientId() != null) {
            r.setPatientId(s.getPatientId());
        } else if (s.getPhone() != null) {
            patientPort.findByPhone(s.getPhone())
                    .ifPresent(p -> r.setPatientId(p.getPatientId()));
        }
        if (r.getPatientId() == null && s.getEmail() != null) {
            patientPort.findByEmail(s.getEmail())
                    .ifPresent(p -> r.setPatientId(p.getPatientId()));
        }
        r.setFirstName(s.getFirstName());
        r.setLastName(s.getLastName());
        r.setInitials(s.getInitials());
        r.setDateOfBirth(s.getDateOfBirth());
        r.setGender(s.getGender());
        r.setEmail(s.getEmail());
        r.setPhone(s.getPhone());
        r.setStatus(s.getStatus());
        r.setActivationStatus(s.getActivationStatus());
        r.setCreatedAt(s.getCreatedAt());
        r.setUpdatedAt(s.getUpdatedAt());
        // Subjects register with phone in Cognito; fall back to email if phone is absent
        String cognitoUsername = (s.getPhone() != null && !s.getPhone().isBlank()) ? s.getPhone() : s.getEmail();
        UserConfirmationResponse cognito = cognitoUserService.getConfirmationStatusSafe(cognitoUsername);
        r.setConfirmed(cognito.isConfirmed());
        r.setUserStatus(cognito.getUserStatus());
        return r;
    }
}