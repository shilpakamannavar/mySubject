package com.study.management.infrastructure.web;

import com.study.management.domain.model.ClinicalStudyStatus;
import com.study.management.domain.model.MedicalCenter;
import com.study.management.domain.model.PrimaryInvestigator;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.StudySiteAssignment;
import com.study.management.domain.model.StudyTeam;
import com.study.management.domain.port.in.ClinicalStudyUseCase;
import com.study.management.infrastructure.web.dto.AssignSiteRequest;
import com.study.management.infrastructure.web.dto.CreateStudyRequest;
import com.study.management.infrastructure.web.dto.PagedResponse;
import com.study.management.infrastructure.web.dto.StudyListItem;
import com.study.management.infrastructure.web.dto.StudyResponse;
import com.study.management.infrastructure.web.dto.StudyStatusPatchRequest;
import com.study.management.infrastructure.web.dto.UpdateStudyDesignRequest;
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

@Tag(name = "Studies", description = "Clinical study management")
@RestController
@RequestMapping("/api/v1/studies")
public class ClinicalStudyController {

    private final ClinicalStudyUseCase studyUseCase;

    public ClinicalStudyController(ClinicalStudyUseCase studyUseCase) {
        this.studyUseCase = studyUseCase;
    }

    @Operation(summary = "List all studies (admin/co-investigator/crc/subject) or all studies (read-only for subject)")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC', 'SUBJECT')")
    @GetMapping
    public ResponseEntity<List<StudyResponse>> list() {
        List<StudyResponse> studies = studyUseCase.getAllStudies().stream()
                .map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(studies);
    }

    @Operation(
        summary = "Design the Study — view",
        description = "Returns studies filtered by the caller's role and phone number (resolved from JWT). " +
                      "ADMIN: all studies. PI: own studies. CO_INVESTIGATOR / CRC / CRO / HOSPITAL: studies whose team they belong to."
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC', 'CRO', 'HOSPITAL')")
    @GetMapping("/design-study")
    public ResponseEntity<List<StudyResponse>> designStudy(@AuthenticationPrincipal Jwt jwt) {
        String callerPhone = resolveCallerId(jwt);
        String callerRole  = jwt.getClaimAsString("custom:role");
        List<StudyEntity> studies = studyUseCase.getDesignStudies(callerPhone, callerRole);
        return ResponseEntity.ok(studies.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @Operation(
        summary = "Design the Study — edit",
        description = "Partial update of study design fields. " +
                      "Allowed: ADMIN (any study), PI (own study only), " +
                      "CO_INVESTIGATOR / CRC who have been granted edit privilege by the PI. " +
                      "Disabled studies cannot be edited. Only send fields you want to change."
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC')")
    @PatchMapping("/{studyId}/design")
    public ResponseEntity<StudyResponse> editStudyDesign(
            @PathVariable UUID studyId,
            @RequestBody UpdateStudyDesignRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        String callerPhone = resolveCallerId(jwt);
        String callerRole  = jwt.getClaimAsString("custom:role");

        StudyEntity updates = new StudyEntity();
        updates.setStudyName(req.getStudyName());
        updates.setTitle(req.getTitle());
        updates.setPhase(req.getPhase());
        updates.setTherapeuticArea(req.getTherapeuticArea());
        updates.setNoOfPatientsToEnroll(req.getNoOfPatientsToEnroll());
        updates.setTotalNumberOfVisits(req.getTotalNumberOfVisits());
        updates.setDateFirstPatientLastVisit(req.getDateFirstPatientLastVisit());
        updates.setDateLastPatientFirstVisit(req.getDateLastPatientFirstVisit());

        StudyEntity saved = studyUseCase.updateStudyDesign(studyId, updates, callerPhone, callerRole);
        StudyResponse resp = toResponse(saved);
        resp.setMessage("Study design updated successfully.");
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Create a new clinical study",
               description = "PI can always create. CO_INVESTIGATOR and CRC require the PI to have granted study-creation privilege via POST /api/v1/study-teams/{trialId}/delegates.")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC')")
    @PostMapping
    public ResponseEntity<StudyResponse> create(@Valid @RequestBody CreateStudyRequest req,
                                                @AuthenticationPrincipal Jwt jwt) {
        String callerUserId = jwt.getClaimAsString("phone_number") != null
                ? jwt.getClaimAsString("phone_number")
                : jwt.getSubject();
        String callerRole = jwt.getClaimAsString("custom:role");

        StudyEntity study = new StudyEntity();
        study.setStudyNumber(req.getStudyNumber());
        study.setStudyName(req.getStudyName());
        study.setTitle(req.getTitle());
        study.setPhase(req.getPhase());
        study.setTherapeuticArea(req.getTherapeuticArea());
        study.setNoOfPatientsToEnroll(req.getNoOfPatientsToEnroll());
        study.setTotalNumberOfVisits(req.getTotalNumberOfVisits());
        study.setDateFirstPatientLastVisit(req.getDateFirstPatientLastVisit());
        study.setDateLastPatientFirstVisit(req.getDateLastPatientFirstVisit());
        StudyTeam team = new StudyTeam();
        team.setTrialId(req.getStudyTeamId());
        study.setStudyTeam(team);
        StudyEntity saved = studyUseCase.createStudy(study, callerUserId, callerRole);
        StudyResponse resp = toResponse(saved);
        resp.setMessage("Study created as INACTIVE. Complete study plan and payment to activate.");
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @Operation(summary = "Get study by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC', 'SUBJECT')")
    @GetMapping("/{studyId}")
    public ResponseEntity<StudyResponse> getById(@PathVariable UUID studyId) {
        StudyEntity study = studyUseCase.getById(studyId);
        return ResponseEntity.ok(toResponse(study));
    }

    @Operation(summary = "Update study status")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR')")
    @PatchMapping("/{studyId}/status")
    public ResponseEntity<StudyResponse> updateStatus(@PathVariable UUID studyId,
                                                       @RequestBody StudyStatusPatchRequest req) {
        StudyEntity updated = studyUseCase.updateStatus(studyId, req.getStatus(), req.getReason());
        return ResponseEntity.ok(toResponse(updated));
    }

    @Operation(summary = "Assign study to a site")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR')")
    @PostMapping("/{studyId}/site-assignments")
    public ResponseEntity<StudySiteAssignment> assignSite(@PathVariable UUID studyId,
                                                           @RequestBody AssignSiteRequest req) {
        StudySiteAssignment assignment = new StudySiteAssignment();
        MedicalCenter center = new MedicalCenter();
        center.setCenterId(req.getCenterId());
        assignment.setMedicalCenter(center);
        if (req.getPiId() != null) {
            PrimaryInvestigator pi = new PrimaryInvestigator();
            pi.setPiId(req.getPiId());
            assignment.setPrimaryInvestigator(pi);
        }
        assignment.setSiteNumber(req.getSiteNumber());
        assignment.setTargetEnrollment(req.getTargetEnrollment());
        assignment.setActivationDate(req.getActivationDate());
        StudySiteAssignment saved = studyUseCase.assignSite(studyId, assignment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    private String resolveCallerId(Jwt jwt) {
        String phone = jwt.getClaimAsString("phone_number");
        if (phone != null && !phone.isBlank()) return phone;
        String email = jwt.getClaimAsString("email");
        if (email != null && !email.isBlank()) return email;
        return jwt.getSubject();
    }

    private StudyResponse toResponse(StudyEntity s) {
        StudyResponse r = new StudyResponse();
        r.setStudyId(s.getStudyId());
        r.setStudyNumber(s.getStudyNumber());
        r.setStudyName(s.getStudyName());
        r.setTitle(s.getTitle());
        r.setPhase(s.getPhase());
        r.setTherapeuticArea(s.getTherapeuticArea());
        r.setNoOfPatientsToEnroll(s.getNoOfPatientsToEnroll());
        r.setTotalNumberOfVisits(s.getTotalNumberOfVisits());
        r.setDateFirstPatientLastVisit(s.getDateFirstPatientLastVisit());
        r.setDateLastPatientFirstVisit(s.getDateLastPatientFirstVisit());
        r.setCalculatedRatePerService(s.getCalculatedRatePerService());
        r.setTotalCost(s.getTotalCost());
        r.setStatus(s.getStatus());
        r.setActivatedAt(s.getActivatedAt());
        r.setDisabledAt(s.getDisabledAt());
        r.setDisabledReason(s.getDisabledReason());
        if (s.getStudyTeam() != null) {
            r.setStudyTeamId(s.getStudyTeam().getTrialId());
            r.setStudyTeamName(s.getStudyTeam().getStudyName());
        }
        r.setCreatedAt(s.getCreatedAt());
        r.setUpdatedAt(s.getUpdatedAt());
        return r;
    }
}
