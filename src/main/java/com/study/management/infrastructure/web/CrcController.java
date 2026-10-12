package com.study.management.infrastructure.web;

import com.study.management.domain.model.ClinicalResearchCoordinator;
import com.study.management.domain.port.in.CrcUseCase;
import com.study.management.infrastructure.cognito.CognitoSyncService;
import com.study.management.infrastructure.cognito.CognitoUserService;
import com.study.management.infrastructure.web.dto.CognitoSyncReport;
import com.study.management.infrastructure.web.dto.CreateCrcRequest;
import com.study.management.infrastructure.web.dto.CrcResponse;
import com.study.management.infrastructure.web.dto.UserConfirmationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "CRCs", description = "Clinical Research Coordinator management")
@RestController
@RequestMapping("/api/v1/crcs")
public class CrcController {

    private final CrcUseCase         crcUseCase;
    private final CognitoUserService cognitoUserService;
    private final CognitoSyncService cognitoSyncService;

    public CrcController(CrcUseCase         crcUseCase,
                         CognitoUserService cognitoUserService,
                         CognitoSyncService cognitoSyncService) {
        this.crcUseCase         = crcUseCase;
        this.cognitoUserService = cognitoUserService;
        this.cognitoSyncService = cognitoSyncService;
    }

    @Operation(summary = "List all CRCs",
               description = "Silently syncs the Cognito crc group into the DB before returning.")
    @GetMapping
    public ResponseEntity<List<CrcResponse>> listAll() {
        cognitoSyncService.syncGroupIntoDb("crc");
        return ResponseEntity.ok(crcUseCase.listAll().stream()
                .map(this::toResponse).collect(Collectors.toList()));
    }

    @Operation(summary = "Sync CRCs from Cognito (with report)",
               description = "Fetches every member of the crc Cognito group, creates missing DB records, " +
                             "and returns a per-user CREATED / SKIPPED / ERROR report.")
    @PostMapping("/sync")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CognitoSyncReport> syncFromCognito() {
        return ResponseEntity.ok(cognitoSyncService.syncSingleRole("crc"));
    }

    @Operation(summary = "Create a new CRC")
    @PostMapping
    public ResponseEntity<CrcResponse> create(@RequestBody CreateCrcRequest req) {
        ClinicalResearchCoordinator crc = new ClinicalResearchCoordinator();
        crc.setFirstName(req.getFirstName());
        crc.setLastName(req.getLastName());
        crc.setTitle(req.getTitle());
        crc.setCredentials(req.getCredentials());
        crc.setEmail(req.getEmail());
        crc.setPhone(req.getPhone());
        crc.setCertificationNumber(req.getCertificationNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(crcUseCase.create(crc)));
    }

    @Operation(summary = "Get CRC by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CrcResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(crcUseCase.getById(id)));
    }

    private CrcResponse toResponse(ClinicalResearchCoordinator crc) {
        CrcResponse r = new CrcResponse();
        r.setCrcId(crc.getCrcId());
        r.setCrcNumber(crc.getCrcNumber());
        r.setFirstName(crc.getFirstName());
        r.setLastName(crc.getLastName());
        r.setTitle(crc.getTitle());
        r.setCredentials(crc.getCredentials());
        r.setEmail(crc.getEmail());
        r.setPhone(crc.getPhone());
        r.setCertificationNumber(crc.getCertificationNumber());
        r.setStatus(crc.getStatus());
        r.setCreatedAt(crc.getCreatedAt());
        r.setUpdatedAt(crc.getUpdatedAt());
        UserConfirmationResponse cognito = cognitoUserService.getConfirmationStatusSafe(crc.getPhone());
        r.setConfirmed(cognito.isConfirmed());
        r.setUserStatus(cognito.getUserStatus());
        return r;
    }
}