package com.study.management.infrastructure.web;

import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.StudyPlanVisit;
import com.study.management.domain.port.in.StudyPlanUseCase;
import com.study.management.domain.port.out.ClinicalStudyPort;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.infrastructure.web.dto.PlanVisitRequest;
import com.study.management.infrastructure.web.dto.PlanVisitResponse;
import com.study.management.infrastructure.web.dto.PutStudyPlanRequest;
import com.study.management.infrastructure.web.dto.StudyPlanResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Study Plan", description = "Study plan visits management")
@RestController
public class StudyPlanController {

    private final StudyPlanUseCase studyPlanUseCase;
    private final ClinicalStudyPort studyPort;

    public StudyPlanController(StudyPlanUseCase studyPlanUseCase, ClinicalStudyPort studyPort) {
        this.studyPlanUseCase = studyPlanUseCase;
        this.studyPort = studyPort;
    }

    @Operation(summary = "Upsert all plan visits for a study")
    @PutMapping("/api/v1/studies/{studyId}/plan-visits")
    public ResponseEntity<StudyPlanResponse> upsertPlanVisits(@PathVariable UUID studyId,
                                                               @RequestBody PutStudyPlanRequest req) {
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
        v.setDurationDays(req.getDurationDays());
        v.setDurationDisplay(req.getDurationDisplay());
        v.setWindowPeriodBefore(req.getWindowPeriodBefore());
        v.setWindowPeriodAfter(req.getWindowPeriodAfter());
        v.setWindowPeriodDisplay(req.getWindowPeriodDisplay());
        v.setDayVisitSpecificCompPattern(req.getDayVisitSpecificCompPattern());
        v.setTargetDayOffset(req.getTargetDayOffset());
        v.setIsMandatory(req.getIsMandatory());
        v.setRequiresFasting(req.getRequiresFasting());
        v.setProcedures(req.getProcedures());
        v.setVisitCost(req.getVisitCost());
        v.setSortOrder(req.getSortOrder());
        return v;
    }

    private PlanVisitResponse toResponse(StudyPlanVisit v) {
        PlanVisitResponse r = new PlanVisitResponse();
        r.setPlanVisitId(v.getPlanVisitId());
        if (v.getStudy() != null) r.setStudyId(v.getStudy().getStudyId());
        r.setVisitNumber(v.getVisitNumber());
        r.setVisitName(v.getVisitName());
        r.setVisitType(v.getVisitType());
        r.setTypeOfVisitOrDose(v.getTypeOfVisitOrDose());
        r.setDurationDays(v.getDurationDays());
        r.setDurationDisplay(v.getDurationDisplay());
        r.setWindowPeriodBefore(v.getWindowPeriodBefore());
        r.setWindowPeriodAfter(v.getWindowPeriodAfter());
        r.setWindowPeriodDisplay(v.getWindowPeriodDisplay());
        r.setDayVisitSpecificCompPattern(v.getDayVisitSpecificCompPattern());
        r.setTargetDayOffset(v.getTargetDayOffset());
        r.setIsMandatory(v.getIsMandatory());
        r.setRequiresFasting(v.getRequiresFasting());
        r.setProcedures(v.getProcedures());
        r.setVisitCost(v.getVisitCost());
        r.setSortOrder(v.getSortOrder());
        r.setCreatedAt(v.getCreatedAt());
        r.setUpdatedAt(v.getUpdatedAt());
        return r;
    }
}
