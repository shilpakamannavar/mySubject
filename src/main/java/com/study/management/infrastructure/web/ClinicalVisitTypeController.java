package com.study.management.infrastructure.web;

import com.study.management.domain.model.ClinicalVisitTypeEntity;
import com.study.management.domain.port.in.ClinicalVisitTypeUseCase;
import com.study.management.infrastructure.web.dto.ClinicalVisitTypeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Clinical Visit Types", description = "Reference data for clinical visit type lookup")
@RestController
@RequestMapping("/api/v1/visit-types")
public class ClinicalVisitTypeController {

    private final ClinicalVisitTypeUseCase useCase;

    public ClinicalVisitTypeController(ClinicalVisitTypeUseCase useCase) {
        this.useCase = useCase;
    }

    @Operation(summary = "Get all active clinical visit types")
    @GetMapping
    public ResponseEntity<List<ClinicalVisitTypeResponse>> getAllActive() {
        List<ClinicalVisitTypeResponse> result = useCase.getAllActive()
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get all clinical visit types including inactive")
    @GetMapping("/all")
    public ResponseEntity<List<ClinicalVisitTypeResponse>> getAll() {
        List<ClinicalVisitTypeResponse> result = useCase.getAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    private ClinicalVisitTypeResponse toResponse(ClinicalVisitTypeEntity e) {
        ClinicalVisitTypeResponse r = new ClinicalVisitTypeResponse();
        r.setCode(e.getCode());
        r.setDisplayName(e.getDisplayName());
        r.setDescription(e.getDescription());
        r.setSortOrder(e.getSortOrder());
        r.setIsActive(e.getIsActive());
        return r;
    }
}
