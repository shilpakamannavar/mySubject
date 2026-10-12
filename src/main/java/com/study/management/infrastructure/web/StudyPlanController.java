package com.study.management.infrastructure.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.management.domain.model.ClinicalStudyStatus;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.StudyPlanVisit;
import com.study.management.domain.model.StudyTeam;
import com.study.management.domain.port.in.StudyPlanUseCase;
import com.study.management.domain.port.out.ClinicalStudyPort;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.infrastructure.web.dto.PlanVisitRequest;
import com.study.management.infrastructure.web.dto.PlanVisitResponse;
import com.study.management.infrastructure.web.dto.PutStudyPlanRequest;
import com.study.management.infrastructure.web.dto.StudyPlanResponse;
import com.study.management.infrastructure.web.dto.StudyTemplateRequest;
import com.study.management.infrastructure.web.dto.StudyTemplateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Study Plan", description = "Study plan visits management")
@RestController
public class StudyPlanController {

    private final StudyPlanUseCase studyPlanUseCase;
    private final ClinicalStudyPort studyPort;

    private final ObjectMapper objectMapper;

    public StudyPlanController(StudyPlanUseCase studyPlanUseCase, ClinicalStudyPort studyPort,
                                ObjectMapper objectMapper) {
        this.studyPlanUseCase = studyPlanUseCase;
        this.studyPort = studyPort;
        this.objectMapper = objectMapper;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR')")
    @Operation(summary = "Upsert all plan visits for a study")
    @PutMapping("/api/v1/studies/{studyId}/plan-visits")
    public ResponseEntity<StudyPlanResponse> upsertPlanVisits(@PathVariable UUID studyId,
                                                               @Valid @RequestBody PutStudyPlanRequest req) {
        List<StudyPlanVisit> visits = req.getVisits().stream().map(this::fromRequest).collect(Collectors.toList());
        List<StudyPlanVisit> saved = studyPlanUseCase.upsertPlanVisits(studyId, visits);

        StudyEntity study = studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));
        StudyPlanResponse resp = new StudyPlanResponse();
        resp.setStudyId(studyId);
        resp.setStudyName(study.getStudyName());
        resp.setCalculatedRatePerService(study.getCalculatedRatePerService());
        resp.setTotalCost(study.getTotalCost());
        resp.setVisits(saved.stream().map(this::toResponse).collect(Collectors.toList()));
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "List plan visits for a study")
    @GetMapping("/api/v1/studies/{studyId}/plan-visits")
    public ResponseEntity<List<PlanVisitResponse>> listPlanVisits(@PathVariable UUID studyId) {
        List<StudyPlanVisit> visits = studyPlanUseCase.getPlanVisits(studyId);
        return ResponseEntity.ok(visits.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    private StudyPlanVisit fromRequest(PlanVisitRequest req) {
        StudyPlanVisit v = new StudyPlanVisit();
        v.setVisitNumber(req.getVisitNumber());
        v.setVisitName(req.getVisitName());
        v.setVisitType(req.getVisitType());
        v.setTypeOfVisitOrDose(req.getTypeOfVisitOrDose());
        v.setDurationValue(req.getDurationValue());
        v.setDurationUnit(req.getDurationUnit());
        v.setDurationDays(req.getDurationDays());
        v.setDurationDisplay(req.getDurationDisplay());
        v.setWindowPeriodBefore(req.getWindowPeriodBefore());
        v.setWindowPeriodAfter(req.getWindowPeriodAfter());
        v.setWindowPeriodDisplay(req.getWindowPeriodDisplay());
        v.setDayVisitSpecificCompPattern(req.getDayVisitSpecificCompPattern());
        v.setTargetDayOffset(req.getTargetDayOffset());
        v.setIsMandatory(req.getIsMandatory());
        v.setRequiresFasting(req.getRequiresFasting());
        v.setPatientInstruction(req.getPatientInstruction());
        if (req.getProcedures() != null) {
            try {
                v.setProcedures(objectMapper.writeValueAsString(req.getProcedures()));
            } catch (JsonProcessingException e) {
                v.setProcedures("[]");
            }
        }
        v.setVisitCost(req.getVisitCost());
        v.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : req.getVisitNumber());
        return v;
    }

    // ── Study Template endpoints ──────────────────────────────────────────

    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC')")
    @Operation(summary = "Create study template with header fields and visit plan (up to 20 visits)")
    @PostMapping("/api/v1/studies/template")
    public ResponseEntity<StudyTemplateResponse> createStudyTemplate(
            @Valid @RequestBody StudyTemplateRequest req) {
        StudyEntity study = templateToStudyEntity(req);
        List<StudyPlanVisit> visits = templateVisitsToEntities(req.getVisits());
        StudyEntity saved = studyPlanUseCase.createStudyTemplate(study, visits);
        List<StudyPlanVisit> savedVisits = studyPlanUseCase.getPlanVisits(saved.getStudyId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toTemplateResponse(saved, savedVisits));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC')")
    @Operation(summary = "Update study template header and visit plan")
    @PutMapping("/api/v1/studies/{studyId}/template")
    public ResponseEntity<StudyTemplateResponse> updateStudyTemplate(
            @PathVariable UUID studyId,
            @Valid @RequestBody StudyTemplateRequest req) {
        StudyEntity updates = templateToStudyEntity(req);
        List<StudyPlanVisit> visits = templateVisitsToEntities(req.getVisits());
        StudyEntity saved = studyPlanUseCase.updateStudyTemplate(studyId, updates, visits);
        List<StudyPlanVisit> savedVisits = studyPlanUseCase.getPlanVisits(saved.getStudyId());
        return ResponseEntity.ok(toTemplateResponse(saved, savedVisits));
    }

    @Operation(summary = "Get study template (header + visit plan)")
    @GetMapping("/api/v1/studies/{studyId}/template")
    public ResponseEntity<StudyTemplateResponse> getStudyTemplate(@PathVariable UUID studyId) {
        StudyEntity study = studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));
        List<StudyPlanVisit> visits = studyPlanUseCase.getPlanVisits(studyId);
        return ResponseEntity.ok(toTemplateResponse(study, visits));
    }

    private StudyEntity templateToStudyEntity(StudyTemplateRequest req) {
        StudyEntity s = new StudyEntity();
        s.setStudyNumber(req.getStudyNumber());
        s.setIndication(req.getIndication());
        s.setNoOfPatientsToEnroll(req.getNoOfPatientsToEnroll());
        s.setTotalNumberOfVisits(req.getTotalNumberOfVisits());
        if (req.getStudyTeamId() != null) {
            StudyTeam team = new StudyTeam();
            team.setTrialId(req.getStudyTeamId());
            s.setStudyTeam(team);
        }
        return s;
    }

    private List<StudyPlanVisit> templateVisitsToEntities(
            List<StudyTemplateRequest.VisitTemplateItem> items) {
        if (items == null) return List.of();
        return items.stream().map(item -> {
            StudyPlanVisit v = new StudyPlanVisit();
            v.setVisitNumber(item.getVisitNumber());
            v.setVisitName(item.getVisitName() != null ? item.getVisitName()
                    : "Visit " + item.getVisitNumber());
            v.setVisitType(item.getVisitType());
            v.setDurationValue(item.getDurationValue());
            v.setDurationUnit(item.getDurationUnit());
            v.setTargetDayOffset(item.getTargetDayOffset());
            v.setWindowPeriodBefore(item.getWindowPeriodBefore());
            v.setWindowPeriodAfter(item.getWindowPeriodAfter());
            v.setPatientInstruction(item.getPatientInstruction());
            v.setSortOrder(item.getVisitNumber());
            return v;
        }).collect(Collectors.toList());
    }

    private StudyTemplateResponse toTemplateResponse(StudyEntity study, List<StudyPlanVisit> visits) {
        StudyTemplateResponse r = new StudyTemplateResponse();
        r.setStudyId(study.getStudyId());
        r.setStudyNumber(study.getStudyNumber());
        r.setIndication(study.getIndication());
        r.setNoOfPatientsToEnroll(study.getNoOfPatientsToEnroll());
        r.setTotalNumberOfVisits(study.getTotalNumberOfVisits());
        r.setStatus(study.getStatus());
        r.setCreatedAt(study.getCreatedAt());
        r.setVisits(visits.stream().map(v -> {
            StudyTemplateResponse.VisitTemplateItem item = new StudyTemplateResponse.VisitTemplateItem();
            item.setPlanVisitId(v.getPlanVisitId());
            item.setVisitNumber(v.getVisitNumber());
            item.setVisitName(v.getVisitName());
            item.setVisitType(v.getVisitType());
            item.setDurationValue(v.getDurationValue());
            item.setDurationUnit(v.getDurationUnit());
            item.setTargetDayOffset(v.getTargetDayOffset());
            item.setWindowPeriodBefore(v.getWindowPeriodBefore());
            item.setWindowPeriodAfter(v.getWindowPeriodAfter());
            item.setPatientInstruction(v.getPatientInstruction());
            return item;
        }).collect(Collectors.toList()));
        return r;
    }

    private PlanVisitResponse toResponse(StudyPlanVisit v) {
        PlanVisitResponse r = new PlanVisitResponse();
        r.setPlanVisitId(v.getPlanVisitId());
        if (v.getStudy() != null) r.setStudyId(v.getStudy().getStudyId());
        r.setVisitNumber(v.getVisitNumber());
        r.setVisitName(v.getVisitName());
        r.setVisitType(v.getVisitType());
        r.setTypeOfVisitOrDose(v.getTypeOfVisitOrDose());
        r.setDurationValue(v.getDurationValue());
        r.setDurationUnit(v.getDurationUnit());
        r.setDurationDays(v.getDurationDays());
        r.setDurationDisplay(v.getDurationDisplay());
        r.setWindowPeriodBefore(v.getWindowPeriodBefore());
        r.setWindowPeriodAfter(v.getWindowPeriodAfter());
        r.setWindowPeriodDisplay(v.getWindowPeriodDisplay());
        r.setDayVisitSpecificCompPattern(v.getDayVisitSpecificCompPattern());
        r.setTargetDayOffset(v.getTargetDayOffset());
        r.setIsMandatory(v.getIsMandatory());
        r.setRequiresFasting(v.getRequiresFasting());
        r.setPatientInstruction(v.getPatientInstruction());
        r.setProcedures(v.getProcedures());
        r.setVisitCost(v.getVisitCost());
        r.setSortOrder(v.getSortOrder());
        r.setCreatedAt(v.getCreatedAt());
        r.setUpdatedAt(v.getUpdatedAt());
        return r;
    }
}
