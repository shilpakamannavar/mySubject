package com.study.management.infrastructure.web;

import com.study.management.domain.model.ClinicalStudyStatus;
import com.study.management.domain.model.MedicalCenter;
import com.study.management.domain.model.PrimaryInvestigator;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.StudySiteAssignment;
import com.study.management.domain.port.in.ClinicalStudyUseCase;
import com.study.management.infrastructure.web.dto.AssignSiteRequest;
import com.study.management.infrastructure.web.dto.CreateStudyRequest;
import com.study.management.infrastructure.web.dto.PagedResponse;
import com.study.management.infrastructure.web.dto.StudyListItem;
import com.study.management.infrastructure.web.dto.StudyResponse;
import com.study.management.infrastructure.web.dto.StudyStatusPatchRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "Create a new clinical study")
    @PostMapping
    public ResponseEntity<StudyResponse> create(@RequestBody CreateStudyRequest req) {
        StudyEntity study = new StudyEntity();
        study.setStudyNumber(req.getStudyNumber());
        study.setStudyName(req.getStudyName());
        study.setIndication(req.getIndication());
        study.setPhase(req.getPhase());
        study.setProtocolNumber(req.getProtocolNumber());
        study.setSponsorName(req.getSponsorName());
        study.setTherapeuticArea(req.getTherapeuticArea());
        study.setNoOfPatientsToEnroll(req.getNoOfPatientsToEnroll());
        study.setTotalNumberOfVisits(req.getTotalNumberOfVisits());
        study.setDateFirstPatientScreening(req.getDateFirstPatientScreening());
        study.setDateFirstPatientLastVisit(req.getDateFirstPatientLastVisit());
        study.setDateLastPatientFirstVisit(req.getDateLastPatientFirstVisit());
        study.setDateLastPatientLastVisit(req.getDateLastPatientLastVisit());
        if (req.getCreatedByPiId() != null) {
            PrimaryInvestigator pi = new PrimaryInvestigator();
            pi.setPiId(req.getCreatedByPiId());
            study.setCreatedByPi(pi);
        }
        StudyEntity saved = studyUseCase.createStudy(study);
        StudyResponse resp = toResponse(saved);
        resp.setMessage("Study created as INACTIVE. Complete study plan and payment to activate.");
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @Operation(summary = "Get study by ID")
    @GetMapping("/{studyId}")
    public ResponseEntity<StudyResponse> getById(@PathVariable UUID studyId) {
        StudyEntity study = studyUseCase.getById(studyId);
        return ResponseEntity.ok(toResponse(study));
    }

    @Operation(summary = "Update study status")
    @PatchMapping("/{studyId}/status")
    public ResponseEntity<StudyResponse> updateStatus(@PathVariable UUID studyId,
                                                       @RequestBody StudyStatusPatchRequest req) {
        StudyEntity updated = studyUseCase.updateStatus(studyId, req.getStatus(), req.getReason());
        return ResponseEntity.ok(toResponse(updated));
    }

    @Operation(summary = "Assign study to a site")
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

    private StudyResponse toResponse(StudyEntity s) {
        StudyResponse r = new StudyResponse();
        r.setStudyId(s.getStudyId());
        r.setStudyNumber(s.getStudyNumber());
        r.setStudyName(s.getStudyName());
        r.setIndication(s.getIndication());
        r.setPhase(s.getPhase());
        r.setProtocolNumber(s.getProtocolNumber());
        r.setSponsorName(s.getSponsorName());
        r.setTherapeuticArea(s.getTherapeuticArea());
        r.setNoOfPatientsToEnroll(s.getNoOfPatientsToEnroll());
        r.setTotalNumberOfVisits(s.getTotalNumberOfVisits());
        r.setDateFirstPatientScreening(s.getDateFirstPatientScreening());
        r.setDateFirstPatientLastVisit(s.getDateFirstPatientLastVisit());
        r.setDateLastPatientFirstVisit(s.getDateLastPatientFirstVisit());
        r.setDateLastPatientLastVisit(s.getDateLastPatientLastVisit());
        r.setCalculatedRatePerService(s.getCalculatedRatePerService());
        r.setTotalCost(s.getTotalCost());
        r.setStatus(s.getStatus());
        r.setActivatedAt(s.getActivatedAt());
        r.setDisabledAt(s.getDisabledAt());
        r.setDisabledReason(s.getDisabledReason());
        if (s.getCreatedByPi() != null) {
            r.setCreatedByPiId(s.getCreatedByPi().getPiId());
            r.setCreatedByPiName(s.getCreatedByPi().getFirstName() + " " + s.getCreatedByPi().getLastName());
        }
        r.setCreatedAt(s.getCreatedAt());
        r.setUpdatedAt(s.getUpdatedAt());
        return r;
    }
}
