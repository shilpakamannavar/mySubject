package com.study.management.infrastructure.web;

import com.study.management.domain.model.ClinicalStudyStatus;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.port.in.ClinicalStudyUseCase;
import com.study.management.infrastructure.web.dto.PagedResponse;
import com.study.management.infrastructure.web.dto.StudyListItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Investigators", description = "PI study access")
@RestController
public class MyStudiesController {

    private final ClinicalStudyUseCase studyUseCase;

    public MyStudiesController(ClinicalStudyUseCase studyUseCase) {
        this.studyUseCase = studyUseCase;
    }

    @Operation(summary = "Get studies for a PI")
    @GetMapping("/api/v1/investigators/{piId}/studies")
    public ResponseEntity<PagedResponse<StudyListItem>> getMyStudies(
            @PathVariable UUID piId,
            @RequestParam(required = false) ClinicalStudyStatus status,
            @RequestParam(required = false) String indication,
            @RequestParam(required = false) UUID centerId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<StudyEntity> studies = studyUseCase.getStudiesForPi(piId, status);
        // Apply indication filter if provided
        if (indication != null && !indication.isBlank()) {
            studies = studies.stream()
                    .filter(s -> indication.equalsIgnoreCase(s.getIndication()))
                    .collect(Collectors.toList());
        }
        List<StudyListItem> items = studies.stream().map(s -> {
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
        }).collect(Collectors.toList());
        return ResponseEntity.ok(PagedResponse.of(items, page, size, items.size()));
    }
}
