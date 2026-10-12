package com.study.management.infrastructure.web;

import com.study.management.domain.model.PatientEntity;
import com.study.management.domain.model.PatientStatus;
import com.study.management.domain.port.in.PatientUseCase;
import com.study.management.infrastructure.web.dto.CreatePatientRequest;
import com.study.management.infrastructure.web.dto.PagedResponse;
import com.study.management.infrastructure.web.dto.PatientResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Tag(name = "Patients", description = "Patient management")
@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final PatientUseCase patientUseCase;

    public PatientController(PatientUseCase patientUseCase) {
        this.patientUseCase = patientUseCase;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CRC', 'CO_INVESTIGATOR')")
    @Operation(summary = "Create a new patient")
    @PostMapping
    public ResponseEntity<PatientResponse> create(@RequestBody CreatePatientRequest req) {
        PatientEntity patient = new PatientEntity();
        patient.setFirstName(req.getFirstName());
        patient.setLastName(req.getLastName());
        patient.setInitials(req.getInitials());
        patient.setDateOfBirth(req.getDateOfBirth());
        patient.setGender(req.getGender());
        patient.setRace(req.getRace());
        patient.setEthnicity(req.getEthnicity());
        patient.setPhone(req.getPhone());
        patient.setEmail(req.getEmail());
        patient.setEmergencyContact(req.getEmergencyContact());
        patient.setEmergencyPhone(req.getEmergencyPhone());
        PatientEntity saved = patientUseCase.createPatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @Operation(summary = "Search patients")
    @GetMapping
    public ResponseEntity<PagedResponse<PatientResponse>> search(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) PatientStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<PatientEntity> patientPage = patientUseCase.searchPatients(
                search, status, PageRequest.of(page - 1, size));
        return ResponseEntity.ok(PagedResponse.of(
                patientPage.getContent().stream().map(this::toResponse).collect(Collectors.toList()),
                page, size, patientPage.getTotalElements()));
    }

    private PatientResponse toResponse(PatientEntity p) {
        PatientResponse r = new PatientResponse();
        r.setPatientId(p.getPatientId());
        r.setPatientNumber(p.getPatientNumber());
        r.setFirstName(p.getFirstName());
        r.setLastName(p.getLastName());
        r.setInitials(p.getInitials());
        r.setDateOfBirth(p.getDateOfBirth());
        r.setGender(p.getGender());
        r.setRace(p.getRace());
        r.setEthnicity(p.getEthnicity());
        r.setPhone(p.getPhone());
        r.setEmail(p.getEmail());
        r.setEmergencyContact(p.getEmergencyContact());
        r.setEmergencyPhone(p.getEmergencyPhone());
        r.setStatus(p.getStatus());
        r.setCreatedAt(p.getCreatedAt());
        r.setUpdatedAt(p.getUpdatedAt());
        return r;
    }
}
