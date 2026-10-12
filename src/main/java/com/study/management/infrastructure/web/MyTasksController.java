package com.study.management.infrastructure.web;

import com.study.management.domain.model.PatientVisit;
import com.study.management.domain.model.VisitStatus;
import com.study.management.domain.port.out.VisitPort;
import com.study.management.infrastructure.web.dto.CalendarDayResponse;
import com.study.management.infrastructure.web.dto.TaskItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "My Tasks", description = "Consolidated cross-study task/schedule calendar for clinical team members")
@RestController
public class MyTasksController {

    private final VisitPort visitPort;

    public MyTasksController(VisitPort visitPort) {
        this.visitPort = visitPort;
    }

    @PreAuthorize("hasAnyRole('PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'ADMIN', 'CRO', 'CRC')")
    @Operation(
        summary = "Get my task list",
        description = "Returns a flat sorted list of visits across all studies the caller is a team member of. "
            + "Default window is ±14 days from today. Sortable by due-today → upcoming → overdue → completed. "
            + "Filterable by studyId, status, and date range."
    )
    @GetMapping("/api/v1/my-tasks")
    public ResponseEntity<List<TaskItem>> getMyTasks(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) UUID studyId,
            @RequestParam(required = false) String status) {

        String phone = resolveCallerPhone(jwt);
        LocalDate rangeFrom = from != null ? from : LocalDate.now().minusDays(14);
        LocalDate rangeTo   = to   != null ? to   : LocalDate.now().plusDays(14);
        VisitStatus statusFilter = parseStatus(status);

        List<PatientVisit> visits = visitPort.findByTeamMemberPhone(phone, rangeFrom, rangeTo, studyId, statusFilter);
        List<TaskItem> tasks = visits.stream()
                .map(this::toTaskItem)
                .sorted(taskComparator())
                .collect(Collectors.toList());

        return ResponseEntity.ok(tasks);
    }

    @PreAuthorize("hasAnyRole('PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'ADMIN', 'CRO', 'CRC')")
    @Operation(
        summary = "Get my calendar view",
        description = "Returns visits grouped by date across all studies the caller is a team member of. "
            + "Default window is ±14 days (5-week rolling view). Each day entry lists all visits scheduled on that date."
    )
    @GetMapping("/api/v1/my-tasks/calendar")
    public ResponseEntity<List<CalendarDayResponse>> getMyCalendar(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        String phone = resolveCallerPhone(jwt);
        LocalDate rangeFrom = from != null ? from : LocalDate.now().minusDays(14);
        LocalDate rangeTo   = to   != null ? to   : LocalDate.now().plusDays(14);

        List<PatientVisit> visits = visitPort.findByTeamMemberPhone(phone, rangeFrom, rangeTo, null, null);

        // Group by scheduledDate preserving date order
        Map<LocalDate, List<PatientVisit>> byDate = visits.stream()
                .collect(Collectors.groupingBy(PatientVisit::getScheduledDate, LinkedHashMap::new, Collectors.toList()));

        // Fill every day in the range so the calendar has no gaps
        List<CalendarDayResponse> calendar = new ArrayList<>();
        for (LocalDate d = rangeFrom; !d.isAfter(rangeTo); d = d.plusDays(1)) {
            List<PatientVisit> dayVisits = byDate.getOrDefault(d, List.of());
            List<TaskItem> tasks = dayVisits.stream()
                    .map(this::toTaskItem)
                    .sorted(taskComparator())
                    .collect(Collectors.toList());

            CalendarDayResponse day = new CalendarDayResponse();
            day.setDate(d);
            day.setDayOfWeek(d.getDayOfWeek().toString());
            day.setTaskCount(tasks.size());
            day.setTasks(tasks);
            calendar.add(day);
        }

        return ResponseEntity.ok(calendar);
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private String resolveCallerPhone(Jwt jwt) {
        String phone = jwt.getClaimAsString("phone_number");
        if (phone == null) phone = jwt.getClaimAsString("email");
        return phone;
    }

    private VisitStatus parseStatus(String status) {
        if (status == null || status.isBlank()) return null;
        try {
            return VisitStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private TaskItem toTaskItem(PatientVisit v) {
        TaskItem item = new TaskItem();
        item.setVisitId(v.getVisitId());
        item.setScheduledDate(v.getScheduledDate());
        item.setWindowOpenDate(v.getWindowOpenDate());
        item.setWindowCloseDate(v.getWindowCloseDate());
        item.setVisitStatus(v.getVisitStatus());
        item.setDisplayColor(computeDisplayColor(v));
        item.setWatermark(computeWatermark(v));

        if (v.getPlanVisit() != null) {
            item.setVisitNumber(v.getPlanVisit().getVisitNumber());
            item.setVisitName(v.getPlanVisit().getVisitName());
        }

        if (v.getEnrollment() != null) {
            item.setEnrollmentId(v.getEnrollment().getEnrollmentId());
            item.setSubjectNumber(v.getEnrollment().getSubjectNumber());

            if (v.getEnrollment().getStudy() != null) {
                item.setStudyId(v.getEnrollment().getStudy().getStudyId());
                item.setStudyNumber(v.getEnrollment().getStudy().getStudyNumber());
                item.setStudyName(v.getEnrollment().getStudy().getStudyName());
            }

            if (v.getEnrollment().getPatient() != null) {
                item.setPatientNumber(v.getEnrollment().getPatient().getPatientNumber());
                item.setPatientInitials(v.getEnrollment().getPatient().getInitials());
            }
        }

        return item;
    }

    private String computeDisplayColor(PatientVisit visit) {
        return switch (visit.getVisitStatus()) {
            case COMPLETED -> "GREEN";
            case MISSED    -> "RED";
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
        if (visit.getVisitStatus() == VisitStatus.MISSED)    return "MISSED";
        if (visit.getVisitStatus() == VisitStatus.CANCELLED) return "CANCELLED";
        if (visit.getVisitStatus() == VisitStatus.COMPLETED) return "DONE";
        return null;
    }

    // Sort: DUE TODAY (YELLOW) > UPCOMING (WHITE/future) > OVERDUE (RED, not missed) > COMPLETED/CANCELLED
    // Within each bucket: date ASC, then studyNumber, then patientNumber
    private Comparator<TaskItem> taskComparator() {
        return Comparator
                .comparingInt((TaskItem t) -> sortPriority(t))
                .thenComparing(t -> t.getScheduledDate() != null ? t.getScheduledDate() : LocalDate.MAX)
                .thenComparing(t -> t.getStudyNumber() != null ? t.getStudyNumber() : "")
                .thenComparing(t -> t.getPatientNumber() != null ? t.getPatientNumber() : "");
    }

    private int sortPriority(TaskItem t) {
        if ("YELLOW".equals(t.getDisplayColor())) return 0; // due today / within 7 days
        if (t.getVisitStatus() == VisitStatus.SCHEDULED || t.getVisitStatus() == VisitStatus.UPCOMING) {
            if ("WHITE".equals(t.getDisplayColor())) return 1; // future upcoming
        }
        if ("RED".equals(t.getDisplayColor()) &&
                (t.getVisitStatus() == VisitStatus.SCHEDULED || t.getVisitStatus() == VisitStatus.UPCOMING))
            return 2; // overdue (past due, not yet marked missed)
        if (t.getVisitStatus() == VisitStatus.MISSED)    return 3;
        if (t.getVisitStatus() == VisitStatus.COMPLETED) return 4;
        return 5;
    }
}
