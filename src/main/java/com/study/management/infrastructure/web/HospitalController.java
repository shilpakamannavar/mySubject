package com.study.management.infrastructure.web;

import com.study.management.domain.model.Hospital;
import com.study.management.domain.port.in.HospitalUseCase;
import com.study.management.infrastructure.cognito.CognitoSyncService;
import com.study.management.infrastructure.cognito.CognitoUserService;
import com.study.management.infrastructure.web.dto.CognitoSyncReport;
import com.study.management.infrastructure.web.dto.CreateHospitalRequest;
import com.study.management.infrastructure.web.dto.HospitalResponse;
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

@Tag(name = "Hospitals", description = "Hospital management")
@RestController
@RequestMapping("/api/v1/hospitals")
public class HospitalController {

    private final HospitalUseCase   hospitalUseCase;
    private final CognitoUserService cognitoUserService;
    private final CognitoSyncService cognitoSyncService;

    public HospitalController(HospitalUseCase   hospitalUseCase,
                               CognitoUserService cognitoUserService,
                               CognitoSyncService cognitoSyncService) {
        this.hospitalUseCase    = hospitalUseCase;
        this.cognitoUserService = cognitoUserService;
        this.cognitoSyncService = cognitoSyncService;
    }

    @Operation(summary = "List all hospitals",
               description = "Silently syncs the Cognito hospital group into the DB before returning.")
    @GetMapping
    public ResponseEntity<List<HospitalResponse>> listAll() {
        cognitoSyncService.syncGroupIntoDb("hospital");
        return ResponseEntity.ok(hospitalUseCase.listAll().stream()
                .map(this::toResponse).collect(Collectors.toList()));
    }

    @Operation(summary = "Sync hospitals from Cognito (with report)",
               description = "Fetches every member of the hospital Cognito group, creates missing DB records, " +
                             "and returns a per-user CREATED / SKIPPED / ERROR report.")
    @PostMapping("/sync")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CognitoSyncReport> syncFromCognito() {
        return ResponseEntity.ok(cognitoSyncService.syncSingleRole("hospital"));
    }

    @Operation(summary = "Create a new hospital")
    @PostMapping
    public ResponseEntity<HospitalResponse> create(@RequestBody CreateHospitalRequest req) {
        Hospital hospital = new Hospital();
        hospital.setHospitalName(req.getHospitalName());
        hospital.setContactPerson(req.getContactPerson());
        hospital.setEmail(req.getEmail());
        hospital.setPhone(req.getPhone());
        hospital.setAddress(req.getAddress());
        hospital.setRegistrationNumber(req.getRegistrationNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(hospitalUseCase.create(hospital)));
    }

    @Operation(summary = "Get hospital by ID")
    @GetMapping("/{id}")
    public ResponseEntity<HospitalResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(hospitalUseCase.getById(id)));
    }

    private HospitalResponse toResponse(Hospital h) {
        HospitalResponse r = new HospitalResponse();
        r.setHospitalId(h.getHospitalId());
        r.setHospitalNumber(h.getHospitalNumber());
        r.setHospitalName(h.getHospitalName());
        r.setContactPerson(h.getContactPerson());
        r.setEmail(h.getEmail());
        r.setPhone(h.getPhone());
        r.setAddress(h.getAddress());
        r.setRegistrationNumber(h.getRegistrationNumber());
        r.setStatus(h.getStatus());
        r.setCreatedAt(h.getCreatedAt());
        r.setUpdatedAt(h.getUpdatedAt());
        UserConfirmationResponse cognito = cognitoUserService.getConfirmationStatusSafe(h.getPhone());
        r.setConfirmed(cognito.isConfirmed());
        r.setUserStatus(cognito.getUserStatus());
        return r;
    }
}