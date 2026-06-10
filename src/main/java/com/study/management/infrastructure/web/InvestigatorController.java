package com.study.management.infrastructure.web;

import com.study.management.domain.model.AssignmentStatus;
import com.study.management.domain.model.ClinicalStudyStatus;
import com.study.management.domain.model.MedicalCenter;
import com.study.management.domain.model.PiCenterAssignment;
import com.study.management.domain.model.PrimaryInvestigator;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.port.in.InvestigatorUseCase;
import com.study.management.domain.port.out.ClinicalStudyPort;
import com.study.management.infrastructure.web.dto.AssignPiToCenterRequest;
import com.study.management.infrastructure.web.dto.AssignmentResponse;
import com.study.management.infrastructure.web.dto.CreatePiRequest;
import com.study.management.infrastructure.web.dto.PiDashboardResponse;
import com.study.management.infrastructure.web.dto.PiResponse;
import com.study.management.infrastructure.web.dto.StudyListItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Investigators", description = "Primary Investigators management")
@RestController
@RequestMapping("/api/v1/investigators")
public class InvestigatorController {

    private final InvestigatorUseCase investigatorUseCase;
    private final ClinicalStudyPort studyPort;

    public InvestigatorController(InvestigatorUseCase investigatorUseCase, ClinicalStudyPort studyPort) {
        this.investigatorUseCase = investigatorUseCase;
        this.studyPort = studyPort;
    }

    @Operation(summary = "Create a new primary investigator")
    @PostMapping
    public ResponseEntity<PiResponse> create(@RequestBody CreatePiRequest req) {
        PrimaryInvestigator pi = new PrimaryInvestigator();
        pi.setPiNumber(req.getPiNumber());
        pi.setFirstName(req.getFirstName());
        pi.setLastName(req.getLastName());
        pi.setTitle(req.getTitle());
        pi.setCredentials(req.getCredentials());
        pi.setSpecialty(req.getSpecialty());
        pi.setEmail(req.getEmail());
        pi.setPhone(req.getPhone());
        pi.setNpiNumber(req.getNpiNumber());
        pi.setLicenseNumber(req.getLicenseNumber());
        pi.setLicenseState(req.getLicenseState());
        pi.setCvDocumentUrl(req.getCvDocumentUrl());
        PrimaryInvestigator saved = investigatorUseCase.createInvestigator(pi);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @Operation(summary = "Assign PI to a center")
    @PostMapping("/{piId}/center-assignments")
    public ResponseEntity<AssignmentResponse> assignToCenter(@PathVariable UUID piId,
                                                              @RequestBody AssignPiToCenterRequest req) {
        PiCenterAssignment assignment = new PiCenterAssignment();
        MedicalCenter center = new MedicalCenter();
        center.setCenterId(req.getCenterId());
        assignment.setMedicalCenter(center);
        assignment.setRole(req.getRole());
        assignment.setIsPrimarySite(req.getIsPrimarySite());
        assignment.setAssignmentDate(req.getAssignmentDate());
        PiCenterAssignment saved = investigatorUseCase.assignToCenter(piId, assignment);
        return ResponseEntity.status(HttpStatus.CREATED).body(toAssignmentResponse(saved));
    }

    @Operation(summary = "Get PI dashboard")
    @GetMapping("/{piId}/dashboard")
    public ResponseEntity<PiDashboardResponse> getDashboard(@PathVariable UUID piId) {
        PrimaryInvestigator pi = investigatorUseCase.getById(piId);
        List<PiCenterAssignment> assignments = investigatorUseCase.getAssignments(piId, null);
        List<StudyEntity> allStudies = studyPort.findByPi(pi);
        List<StudyEntity> activeStudies = studyPort.findByPiAndStatus(pi, ClinicalStudyStatus.ACTIVE);

        PiDashboardResponse dashboard = new PiDashboardResponse();
        dashboard.setPiId(pi.getPiId());
        dashboard.setPiName(pi.getFirstName() + " " + pi.getLastName());
        dashboard.setPiNumber(pi.getPiNumber());
        dashboard.setSpecialty(pi.getSpecialty());
        dashboard.setTotalStudies(allStudies.size());
        dashboard.setActiveStudies(activeStudies.size());
        dashboard.setTotalCenters(assignments.size());

        List<StudyListItem> recentStudies = allStudies.stream().limit(5).map(this::toStudyListItem)
                .collect(Collectors.toList());
        dashboard.setRecentStudies(recentStudies);

        List<AssignmentResponse> assignmentResponses = assignments.stream()
                .map(this::toAssignmentResponse).collect(Collectors.toList());
        dashboard.setCenterAssignments(assignmentResponses);
        return ResponseEntity.ok(dashboard);
    }

    @Operation(summary = "List PI center assignments")
    @GetMapping("/{piId}/center-assignments")
    public ResponseEntity<List<AssignmentResponse>> listAssignments(
            @PathVariable UUID piId,
            @RequestParam(required = false) AssignmentStatus status) {
        List<PiCenterAssignment> assignments = investigatorUseCase.getAssignments(piId, status);
        return ResponseEntity.ok(assignments.stream().map(this::toAssignmentResponse).collect(Collectors.toList()));
    }

    private PiResponse toResponse(PrimaryInvestigator pi) {
        PiResponse r = new PiResponse();
        r.setPiId(pi.getPiId());
        r.setPiNumber(pi.getPiNumber());
        r.setFirstName(pi.getFirstName());
        r.setLastName(pi.getLastName());
        r.setTitle(pi.getTitle());
        r.setCredentials(pi.getCredentials());
        r.setSpecialty(pi.getSpecialty());
        r.setEmail(pi.getEmail());
        r.setPhone(pi.getPhone());
        r.setNpiNumber(pi.getNpiNumber());
        r.setLicenseNumber(pi.getLicenseNumber());
        r.setLicenseState(pi.getLicenseState());
        r.setCvDocumentUrl(pi.getCvDocumentUrl());
        r.setStatus(pi.getStatus());
        r.setCreatedAt(pi.getCreatedAt());
        r.setUpdatedAt(pi.getUpdatedAt());
        return r;
    }

    private AssignmentResponse toAssignmentResponse(PiCenterAssignment a) {
        AssignmentResponse r = new AssignmentResponse();
        r.setAssignmentId(a.getAssignmentId());
        if (a.getPrimaryInvestigator() != null) {
            r.setPiId(a.getPrimaryInvestigator().getPiId());
            r.setPiName(a.getPrimaryInvestigator().getFirstName() + " " + a.getPrimaryInvestigator().getLastName());
        }
        if (a.getMedicalCenter() != null) {
            r.setCenterId(a.getMedicalCenter().getCenterId());
            r.setCenterName(a.getMedicalCenter().getCenterName());
        }
        r.setRole(a.getRole());
        r.setIsPrimarySite(a.getIsPrimarySite());
        r.setAssignmentDate(a.getAssignmentDate());
        r.setEndDate(a.getEndDate());
        r.setStatus(a.getStatus());
        r.setCreatedAt(a.getCreatedAt());
        return r;
    }

    private StudyListItem toStudyListItem(StudyEntity s) {
        StudyListItem item = new StudyListItem();
        item.setStudyId(s.getStudyId());
        item.setStudyNumber(s.getStudyNumber());
        item.setStudyName(s.getStudyName());
        item.setIndication(s.getIndication());
        item.setPhase(s.getPhase());
        item.setSponsorName(s.getSponsorName());
        item.setStatus(s.getStatus());
        item.setNoOfPatientsToEnroll(s.getNoOfPatientsToEnroll());
        item.setTotalCost(s.getTotalCost());
        item.setCreatedAt(s.getCreatedAt());
        return item;
    }
}
