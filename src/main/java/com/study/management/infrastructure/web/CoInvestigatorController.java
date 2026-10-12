package com.study.management.infrastructure.web;

import com.study.management.domain.model.CoInvestigator;
import com.study.management.domain.port.in.CoInvestigatorUseCase;
import com.study.management.infrastructure.cognito.CognitoSyncService;
import com.study.management.infrastructure.cognito.CognitoUserService;
import com.study.management.infrastructure.web.dto.CoInvestigatorResponse;
import com.study.management.infrastructure.web.dto.CognitoSyncReport;
import com.study.management.infrastructure.web.dto.CreateCoInvestigatorRequest;
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

@Tag(name = "Co-Investigators", description = "Co-Investigator management")
@RestController
@RequestMapping("/api/v1/co-investigators")
public class CoInvestigatorController {

    private final CoInvestigatorUseCase coInvestigatorUseCase;
    private final CognitoUserService    cognitoUserService;
    private final CognitoSyncService    cognitoSyncService;

    public CoInvestigatorController(CoInvestigatorUseCase coInvestigatorUseCase,
                                     CognitoUserService    cognitoUserService,
                                     CognitoSyncService    cognitoSyncService) {
        this.coInvestigatorUseCase = coInvestigatorUseCase;
        this.cognitoUserService    = cognitoUserService;
        this.cognitoSyncService    = cognitoSyncService;
    }

    @Operation(summary = "List all co-investigators",
               description = "Silently syncs the Cognito co-investigator group into the DB before returning.")
    @GetMapping
    public ResponseEntity<List<CoInvestigatorResponse>> listAll() {
        cognitoSyncService.syncGroupIntoDb("co-investigator");
        return ResponseEntity.ok(coInvestigatorUseCase.listAll().stream()
                .map(this::toResponse).collect(Collectors.toList()));
    }

    @Operation(summary = "Sync co-investigators from Cognito (with report)",
               description = "Fetches every member of the co_investigator Cognito group, creates missing DB records, " +
                             "and returns a per-user CREATED / SKIPPED / ERROR report.")
    @PostMapping("/sync")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CognitoSyncReport> syncFromCognito() {
        return ResponseEntity.ok(cognitoSyncService.syncSingleRole("co-investigator"));
    }

    @Operation(summary = "Create a new co-investigator")
    @PostMapping
    public ResponseEntity<CoInvestigatorResponse> create(@RequestBody CreateCoInvestigatorRequest req) {
        CoInvestigator ci = new CoInvestigator();
        ci.setFirstName(req.getFirstName());
        ci.setLastName(req.getLastName());
        ci.setTitle(req.getTitle());
        ci.setCredentials(req.getCredentials());
        ci.setSpecialty(req.getSpecialty());
        ci.setEmail(req.getEmail());
        ci.setPhone(req.getPhone());
        ci.setNpiNumber(req.getNpiNumber());
        ci.setLicenseNumber(req.getLicenseNumber());
        ci.setLicenseState(req.getLicenseState());
        ci.setCvDocumentUrl(req.getCvDocumentUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(coInvestigatorUseCase.create(ci)));
    }

    @Operation(summary = "Get co-investigator by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CoInvestigatorResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(coInvestigatorUseCase.getById(id)));
    }

    private CoInvestigatorResponse toResponse(CoInvestigator ci) {
        CoInvestigatorResponse r = new CoInvestigatorResponse();
        r.setCiId(ci.getCiId());
        r.setCiNumber(ci.getCiNumber());
        r.setFirstName(ci.getFirstName());
        r.setLastName(ci.getLastName());
        r.setTitle(ci.getTitle());
        r.setCredentials(ci.getCredentials());
        r.setSpecialty(ci.getSpecialty());
        r.setEmail(ci.getEmail());
        r.setPhone(ci.getPhone());
        r.setNpiNumber(ci.getNpiNumber());
        r.setLicenseNumber(ci.getLicenseNumber());
        r.setLicenseState(ci.getLicenseState());
        r.setCvDocumentUrl(ci.getCvDocumentUrl());
        r.setStatus(ci.getStatus());
        r.setCreatedAt(ci.getCreatedAt());
        r.setUpdatedAt(ci.getUpdatedAt());
        UserConfirmationResponse cognito = cognitoUserService.getConfirmationStatusSafe(ci.getPhone());
        r.setConfirmed(cognito.isConfirmed());
        r.setUserStatus(cognito.getUserStatus());
        return r;
    }
}