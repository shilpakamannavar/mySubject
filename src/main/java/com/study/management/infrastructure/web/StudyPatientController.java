package com.study.management.infrastructure.web;

import com.study.management.domain.model.StudyPatient;
import com.study.management.domain.port.in.StudyPatientUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Study Patients", description = "Manage patients within an active study")
@RestController
@RequestMapping("/api/studies/{studyId}/patients")
public class StudyPatientController {

    private final StudyPatientUseCase patientUseCase;

    public StudyPatientController(StudyPatientUseCase patientUseCase) {
        this.patientUseCase = patientUseCase;
    }

    @Operation(summary = "Add patient to an ACTIVE study")
    @PostMapping
    public ResponseEntity<StudyPatient> addPatient(
            @PathVariable String studyId,
            @RequestBody PatientRequest req) {
        StudyPatient patient = new StudyPatient();
        patient.setPatientInitial(req.patientInitial());
        patient.setMobileNumber(req.mobileNumber());
        patient.setScreeningDate(req.screeningDate());
        patient.setEnrollmentRandomizationDate(req.enrollmentRandomizationDate());
        return ResponseEntity.ok(patientUseCase.addPatient(studyId, patient));
    }

    @Operation(summary = "List patients in a study")
    @GetMapping
    public ResponseEntity<List<StudyPatient>> getPatients(@PathVariable String studyId) {
        return ResponseEntity.ok(patientUseCase.getPatientsByStudy(studyId));
    }

    @Operation(summary = "Activate a patient")
    @PatchMapping("/{patientNumber}/activate")
    public ResponseEntity<StudyPatient> activate(
            @PathVariable String studyId,
            @PathVariable String patientNumber) {
        return ResponseEntity.ok(patientUseCase.activatePatient(studyId, patientNumber));
    }

    record PatientRequest(
            @Size(max = 3) String patientInitial,
            @Pattern(regexp = "^[0-9]{10}$") String mobileNumber,
            LocalDate screeningDate,
            LocalDate enrollmentRandomizationDate) {}
}
