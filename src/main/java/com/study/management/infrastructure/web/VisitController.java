package com.study.management.infrastructure.web;

import com.study.management.domain.model.ActivationStatus;
import com.study.management.domain.model.PatientEntity;
import com.study.management.domain.model.PatientVisit;
import com.study.management.domain.model.PrimaryInvestigator;
import com.study.management.domain.model.StudyEnrollment;
import com.study.management.domain.model.VisitStatus;
import com.study.management.domain.port.in.VisitUseCase;
import com.study.management.domain.port.out.ClinicalStudyPort;
import com.study.management.domain.port.out.EnrollmentPort;
import com.study.management.domain.port.out.SubjectPort;
import com.study.management.domain.port.out.VisitPort;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.infrastructure.web.dto.CompleteVisitRequest;
import com.study.management.infrastructure.web.dto.RescheduleVisitRequest;
import com.study.management.infrastructure.web.dto.StudyScheduleResponse;
import com.study.management.infrastructure.web.dto.VisitItem;
import com.study.management.infrastructure.web.dto.VisitScheduleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Visits", description = "Patient visit management")
@RestController
public class VisitController {

    private final VisitUseCase visitUseCase;
    private final EnrollmentPort enrollmentPort;
    private final ClinicalStudyPort clinicalStudyPort;
    private final VisitPort visitPort;
    private final SubjectPort subjectPort;

    public VisitController(VisitUseCase visitUseCase,
                           EnrollmentPort enrollmentPort,
                           ClinicalStudyPort clinicalStudyPort,
                           VisitPort visitPort,
                           SubjectPort subjectPort) {
        this.visitUseCase = visitUseCase;
        this.enrollmentPort = enrollmentPort;
        this.clinicalStudyPort = clinicalStudyPort;
        this.visitPort = visitPort;
        this.subjectPort = subjectPort;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC', 'SUBJECT')")
    @Operation(summary = "Get full study schedule — all enrolled subjects and their visits")
    @GetMapping("/api/v1/studies/{studyId}/schedule")
    public ResponseEntity<StudyScheduleResponse> getStudySchedule(@PathVariable UUID studyId) {
        List<PatientVisit> allVisits = visitUseCase.getStudySchedule(studyId);

        // Group visits by enrollmentId, preserving subject-number order from the query
        Map<UUID, List<PatientVisit>> byEnrollment = allVisits.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getEnrollment().getEnrollmentId(),
                        LinkedHashMap::new,
                        Collectors.toList()));

        List<VisitScheduleResponse> subjectSchedules = byEnrollment.entrySet().stream()
                .map(entry -> {
                    StudyEnrollment enrollment = entry.getValue().get(0).getEnrollment();
                    VisitScheduleResponse vsr = new VisitScheduleResponse();
                    vsr.setEnrollmentId(entry.getKey());
                    if (enrollment.getStudy() != null) {
                        vsr.setStudyId(enrollment.getStudy().getStudyId());
                        vsr.setStudyName(enrollment.getStudy().getStudyName());
                    }
                    if (enrollment.getPatient() != null)
                        vsr.setPatientNumber(enrollment.getPatient().getPatientNumber());
                    vsr.setSubjectNumber(enrollment.getSubjectNumber());
                    vsr.setVisits(entry.getValue().stream()
                            .map(this::toVisitItem).collect(Collectors.toList()));
                    return vsr;
                })
                .collect(Collectors.toList());

        var study = clinicalStudyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));

        StudyScheduleResponse resp = new StudyScheduleResponse();
        resp.setStudyId(studyId);
        resp.setStudyName(study.getStudyName());
        resp.setStudyNumber(study.getStudyNumber());
        resp.setTotalSubjectsEnrolled(subjectSchedules.size());
        resp.setSubjectSchedules(subjectSchedules);
        return ResponseEntity.ok(resp);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC', 'SUBJECT')")
    @Operation(summary = "Get visit schedule for a single enrolled subject")
    @GetMapping("/api/v1/studies/{studyId}/enrollments/{enrollmentId}/visits")
    public ResponseEntity<VisitScheduleResponse> getSchedule(@PathVariable UUID studyId,
                                                              @PathVariable UUID enrollmentId) {
        List<PatientVisit> visits = visitUseCase.getVisitSchedule(enrollmentId);
        var enrollment = enrollmentPort.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));

        VisitScheduleResponse resp = new VisitScheduleResponse();
        resp.setEnrollmentId(enrollmentId);
        if (enrollment.getStudy() != null) {
            resp.setStudyId(enrollment.getStudy().getStudyId());
            resp.setStudyName(enrollment.getStudy().getStudyName());
        }
        if (enrollment.getPatient() != null) resp.setPatientNumber(enrollment.getPatient().getPatientNumber());
        resp.setSubjectNumber(enrollment.getSubjectNumber());
        resp.setVisits(visits.stream().map(this::toVisitItem).collect(Collectors.toList()));
        return ResponseEntity.ok(resp);
    }

    @Operation(
        summary = "Complete a visit",
        description = "Records visit completion. The subject must be activated (via QR code scan) before any visit can be marked complete."
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC')")
    @PatchMapping("/api/v1/visits/{visitId}/complete")
    public ResponseEntity<VisitItem> completeVisit(@PathVariable UUID visitId,
                                                    @RequestBody CompleteVisitRequest req) {
        // Guard: subject must be activated before visits can be recorded
        visitPort.findById(visitId).ifPresent(visit -> {
            StudyEnrollment enrollment = visit.getEnrollment();
            if (enrollment != null && enrollment.getPatient() != null) {
                String patientPhone = enrollment.getPatient().getPhone();
                if (patientPhone != null) {
                    subjectPort.findByPhone(patientPhone).ifPresent(subject -> {
                        if (subject.getActivationStatus() != ActivationStatus.ACTIVE) {
                            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                    "Subject must be activated via QR code scan before visits can be recorded.");
                        }
                    });
                }
            }
        });

        PatientVisit updates = new PatientVisit();
        updates.setActualDate(req.getActualDate());
        updates.setCheckInTime(req.getCheckInTime());
        updates.setCheckOutTime(req.getCheckOutTime());
        updates.setDurationMinutes(req.getDurationMinutes());
        updates.setVitalsRecorded(req.getVitalsRecorded());
        updates.setAdverseEvents(req.getAdverseEvents());
        updates.setLabSamplesTaken(req.getLabSamplesTaken());
        updates.setProceduresDone(req.getProceduresDone());
        updates.setInvestigatorNotes(req.getInvestigatorNotes());
        updates.setCoordinatorNotes(req.getCoordinatorNotes());
        updates.setPatientReported(req.getPatientReported());
        if (req.getPerformedByPiId() != null) {
            PrimaryInvestigator pi = new PrimaryInvestigator();
            pi.setPiId(req.getPerformedByPiId());
            updates.setPerformedByPi(pi);
        }
        PatientVisit updated = visitUseCase.completeVisit(visitId, updates);
        return ResponseEntity.ok(toVisitItem(updated));
    }

    @Operation(summary = "Mark a visit as missed")
    @PatchMapping("/api/v1/visits/{visitId}/missed")
    public ResponseEntity<VisitItem> markMissed(@PathVariable UUID visitId) {
        PatientVisit updated = visitUseCase.markMissed(visitId);
        return ResponseEntity.ok(toVisitItem(updated));
    }

    @Operation(summary = "Reschedule a visit")
    @PatchMapping("/api/v1/visits/{visitId}/reschedule")
    public ResponseEntity<VisitItem> reschedule(@PathVariable UUID visitId,
                                                 @RequestBody RescheduleVisitRequest req) {
        PatientVisit updated = visitUseCase.reschedule(visitId, req.getNewScheduledDate());
        return ResponseEntity.ok(toVisitItem(updated));
    }

    private VisitItem toVisitItem(PatientVisit v) {
        VisitItem item = new VisitItem();
        item.setVisitId(v.getVisitId());
        if (v.getEnrollment() != null) item.setEnrollmentId(v.getEnrollment().getEnrollmentId());
        if (v.getPlanVisit() != null) {
            item.setPlanVisitId(v.getPlanVisit().getPlanVisitId());
            item.setVisitNumber(v.getPlanVisit().getVisitNumber());
            item.setVisitName(v.getPlanVisit().getVisitName());
        }
        item.setScheduledDate(v.getScheduledDate());
        item.setActualDate(v.getActualDate());
        item.setWindowOpenDate(v.getWindowOpenDate());
        item.setWindowCloseDate(v.getWindowCloseDate());
        item.setVisitStatus(v.getVisitStatus());
        item.setDisplayColor(computeDisplayColor(v));
        item.setWatermark(computeWatermark(v));
        return item;
    }

    private String computeDisplayColor(PatientVisit visit) {
        return switch (visit.getVisitStatus()) {
            case COMPLETED -> "GREEN";
            case MISSED -> "RED";
            case CANCELLED -> "GREY";
            case SCHEDULED, UPCOMING -> {
                LocalDate today = LocalDate.now();
                if (visit.getScheduledDate() != null &&
                        !visit.getScheduledDate().isAfter(today.plusDays(7)) &&
                        !visit.getScheduledDate().isBefore(today)) yield "YELLOW";
                else if (visit.getScheduledDate() != null && visit.getScheduledDate().isBefore(today)) yield "RED";
                else yield "WHITE";
            }
            default -> "WHITE";
        };
    }

    private String computeWatermark(PatientVisit visit) {
        if (visit.getVisitStatus() == VisitStatus.MISSED) return "MISSED";
        if (visit.getVisitStatus() == VisitStatus.CANCELLED) return "CANCELLED";
        if (visit.getVisitStatus() == VisitStatus.COMPLETED) return "DONE";
        return null;
    }
}
