package com.study.management.infrastructure.web;

import com.study.management.domain.model.Mpa;
import com.study.management.domain.model.MpaStatus;
import com.study.management.domain.port.in.MpaUseCase;
import com.study.management.domain.port.out.MpaPort;
import com.study.management.infrastructure.web.dto.CreateMpaRequest;
import com.study.management.infrastructure.web.dto.MpaResponse;
import com.study.management.infrastructure.web.dto.PagedResponse;
import com.study.management.infrastructure.web.dto.UpdateMpaRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "MPA", description = "Master Participation Agreements")
@RestController
@RequestMapping("/api/v1/mpa")
public class MpaController {

    private final MpaUseCase mpaUseCase;
    private final MpaPort mpaPort;

    public MpaController(MpaUseCase mpaUseCase, MpaPort mpaPort) {
        this.mpaUseCase = mpaUseCase;
        this.mpaPort = mpaPort;
    }

    @Operation(summary = "Create a new MPA")
    @PostMapping
    public ResponseEntity<MpaResponse> create(@RequestBody CreateMpaRequest req) {
        Mpa mpa = new Mpa();
        mpa.setMpaNumber(req.getMpaNumber());
        mpa.setMpaName(req.getMpaName());
        mpa.setOrganizationName(req.getOrganizationName());
        mpa.setContractType(req.getContractType());
        mpa.setEffectiveDate(req.getEffectiveDate());
        mpa.setExpirationDate(req.getExpirationDate());
        mpa.setContactName(req.getContactName());
        mpa.setContactEmail(req.getContactEmail());
        mpa.setContactPhone(req.getContactPhone());
        mpa.setNotes(req.getNotes());
        Mpa saved = mpaUseCase.createMpa(mpa);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @Operation(summary = "Get MPA by ID")
    @GetMapping("/{mpaId}")
    public ResponseEntity<MpaResponse> getById(@PathVariable UUID mpaId) {
        Mpa mpa = mpaUseCase.getById(mpaId);
        MpaResponse resp = toResponse(mpa);
        resp.setCentersCount(mpaPort.countCenters(mpaId));
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "List all MPAs with optional status filter")
    @GetMapping
    public ResponseEntity<PagedResponse<MpaResponse>> list(
            @RequestParam(required = false) MpaStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<Mpa> mpas = mpaUseCase.listAll(status);
        List<MpaResponse> items = mpas.stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(PagedResponse.of(items, page, size, items.size()));
    }

    @Operation(summary = "Update MPA")
    @PutMapping("/{mpaId}")
    public ResponseEntity<MpaResponse> update(@PathVariable UUID mpaId,
                                               @RequestBody UpdateMpaRequest req) {
        Mpa updates = new Mpa();
        updates.setMpaName(req.getMpaName());
        updates.setExpirationDate(req.getExpirationDate());
        updates.setContactName(req.getContactName());
        updates.setContactEmail(req.getContactEmail());
        updates.setNotes(req.getNotes());
        Mpa updated = mpaUseCase.update(mpaId, updates);
        return ResponseEntity.ok(toResponse(updated));
    }

    private MpaResponse toResponse(Mpa mpa) {
        MpaResponse r = new MpaResponse();
        r.setMpaId(mpa.getMpaId());
        r.setMpaNumber(mpa.getMpaNumber());
        r.setMpaName(mpa.getMpaName());
        r.setOrganizationName(mpa.getOrganizationName());
        r.setContractType(mpa.getContractType());
        r.setEffectiveDate(mpa.getEffectiveDate());
        r.setExpirationDate(mpa.getExpirationDate());
        r.setStatus(mpa.getStatus());
        r.setContactName(mpa.getContactName());
        r.setContactEmail(mpa.getContactEmail());
        r.setContactPhone(mpa.getContactPhone());
        r.setNotes(mpa.getNotes());
        r.setCreatedAt(mpa.getCreatedAt());
        r.setUpdatedAt(mpa.getUpdatedAt());
        return r;
    }
}
