package com.study.management.infrastructure.web;

import com.study.management.domain.model.ContractResearchOrganization;
import com.study.management.domain.port.in.CroUseCase;
import com.study.management.infrastructure.cognito.CognitoSyncService;
import com.study.management.infrastructure.cognito.CognitoUserService;
import com.study.management.infrastructure.web.dto.CognitoSyncReport;
import com.study.management.infrastructure.web.dto.CreateCroRequest;
import com.study.management.infrastructure.web.dto.CroResponse;
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

@Tag(name = "CROs", description = "Contract Research Organization management")
@RestController
@RequestMapping("/api/v1/cros")
public class CroController {

    private final CroUseCase         croUseCase;
    private final CognitoUserService cognitoUserService;
    private final CognitoSyncService cognitoSyncService;

    public CroController(CroUseCase         croUseCase,
                         CognitoUserService cognitoUserService,
                         CognitoSyncService cognitoSyncService) {
        this.croUseCase         = croUseCase;
        this.cognitoUserService = cognitoUserService;
        this.cognitoSyncService = cognitoSyncService;
    }

    @Operation(summary = "List all CROs",
               description = "Silently syncs the Cognito cro group into the DB before returning.")
    @GetMapping
    public ResponseEntity<List<CroResponse>> listAll() {
        cognitoSyncService.syncGroupIntoDb("cro");
        return ResponseEntity.ok(croUseCase.listAll().stream()
                .map(this::toResponse).collect(Collectors.toList()));
    }

    @Operation(summary = "Sync CROs from Cognito (with report)",
               description = "Fetches every member of the cro Cognito group, creates missing DB records, " +
                             "and returns a per-user CREATED / SKIPPED / ERROR report.")
    @PostMapping("/sync")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CognitoSyncReport> syncFromCognito() {
        return ResponseEntity.ok(cognitoSyncService.syncSingleRole("cro"));
    }

    @Operation(summary = "Create a new CRO")
    @PostMapping
    public ResponseEntity<CroResponse> create(@RequestBody CreateCroRequest req) {
        ContractResearchOrganization cro = new ContractResearchOrganization();
        cro.setOrganizationName(req.getOrganizationName());
        cro.setContactPerson(req.getContactPerson());
        cro.setEmail(req.getEmail());
        cro.setPhone(req.getPhone());
        cro.setAddress(req.getAddress());
        cro.setRegistrationNumber(req.getRegistrationNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(croUseCase.create(cro)));
    }

    @Operation(summary = "Get CRO by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CroResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(croUseCase.getById(id)));
    }

    private CroResponse toResponse(ContractResearchOrganization cro) {
        CroResponse r = new CroResponse();
        r.setCroId(cro.getCroId());
        r.setCroNumber(cro.getCroNumber());
        r.setOrganizationName(cro.getOrganizationName());
        r.setContactPerson(cro.getContactPerson());
        r.setEmail(cro.getEmail());
        r.setPhone(cro.getPhone());
        r.setAddress(cro.getAddress());
        r.setRegistrationNumber(cro.getRegistrationNumber());
        r.setStatus(cro.getStatus());
        r.setCreatedAt(cro.getCreatedAt());
        r.setUpdatedAt(cro.getUpdatedAt());
        UserConfirmationResponse cognito = cognitoUserService.getConfirmationStatusSafe(cro.getPhone());
        r.setConfirmed(cognito.isConfirmed());
        r.setUserStatus(cognito.getUserStatus());
        return r;
    }
}