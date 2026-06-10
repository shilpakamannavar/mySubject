package com.study.management.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "patient_visits")
public class PatientVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "visit_id")
    private UUID visitId;

    @ManyToOne
    @JoinColumn(name = "enrollment_id")
    private StudyEnrollment enrollment;

    @ManyToOne
    @JoinColumn(name = "plan_visit_id")
    private StudyPlanVisit planVisit;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "actual_date")
    private LocalDate actualDate;

    @Column(name = "window_open_date")
    private LocalDate windowOpenDate;

    @Column(name = "window_close_date")
    private LocalDate windowCloseDate;

    @Column(name = "check_in_time")
    private LocalTime checkInTime;

    @Column(name = "check_out_time")
    private LocalTime checkOutTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "visit_status")
    private VisitStatus visitStatus;

    @Column(name = "vitals_recorded")
    private Boolean vitalsRecorded;

    @Column(name = "adverse_events")
    private Boolean adverseEvents;

    @Column(name = "lab_samples_taken")
    private Boolean labSamplesTaken;

    @Lob
    @Column(name = "procedures_done")
    private String proceduresDone;

    @Column(name = "investigator_notes")
    private String investigatorNotes;

    @Column(name = "coordinator_notes")
    private String coordinatorNotes;

    @Column(name = "patient_reported")
    private String patientReported;

    @ManyToOne
    @JoinColumn(name = "performed_by_pi_id")
    private PrimaryInvestigator performedByPi;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (updatedAt == null) updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // ── Getters & Setters ──────────────────────────────────────────────

    public UUID getVisitId()                            { return visitId; }
    public void setVisitId(UUID v)                     { this.visitId = v; }

    public StudyEnrollment getEnrollment()              { return enrollment; }
    public void setEnrollment(StudyEnrollment v)       { this.enrollment = v; }

    public StudyPlanVisit getPlanVisit()                { return planVisit; }
    public void setPlanVisit(StudyPlanVisit v)         { this.planVisit = v; }

    public LocalDate getScheduledDate()                 { return scheduledDate; }
    public void setScheduledDate(LocalDate v)          { this.scheduledDate = v; }

    public LocalDate getActualDate()                    { return actualDate; }
    public void setActualDate(LocalDate v)             { this.actualDate = v; }

    public LocalDate getWindowOpenDate()                { return windowOpenDate; }
    public void setWindowOpenDate(LocalDate v)         { this.windowOpenDate = v; }

    public LocalDate getWindowCloseDate()               { return windowCloseDate; }
    public void setWindowCloseDate(LocalDate v)        { this.windowCloseDate = v; }

    public LocalTime getCheckInTime()                   { return checkInTime; }
    public void setCheckInTime(LocalTime v)            { this.checkInTime = v; }

    public LocalTime getCheckOutTime()                  { return checkOutTime; }
    public void setCheckOutTime(LocalTime v)           { this.checkOutTime = v; }

    public Integer getDurationMinutes()                 { return durationMinutes; }
    public void setDurationMinutes(Integer v)          { this.durationMinutes = v; }

    public VisitStatus getVisitStatus()                 { return visitStatus; }
    public void setVisitStatus(VisitStatus v)          { this.visitStatus = v; }

    public Boolean getVitalsRecorded()                  { return vitalsRecorded; }
    public void setVitalsRecorded(Boolean v)           { this.vitalsRecorded = v; }

    public Boolean getAdverseEvents()                   { return adverseEvents; }
    public void setAdverseEvents(Boolean v)            { this.adverseEvents = v; }

    public Boolean getLabSamplesTaken()                 { return labSamplesTaken; }
    public void setLabSamplesTaken(Boolean v)          { this.labSamplesTaken = v; }

    public String getProceduresDone()                   { return proceduresDone; }
    public void setProceduresDone(String v)            { this.proceduresDone = v; }

    public String getInvestigatorNotes()                { return investigatorNotes; }
    public void setInvestigatorNotes(String v)         { this.investigatorNotes = v; }

    public String getCoordinatorNotes()                 { return coordinatorNotes; }
    public void setCoordinatorNotes(String v)          { this.coordinatorNotes = v; }

    public String getPatientReported()                  { return patientReported; }
    public void setPatientReported(String v)           { this.patientReported = v; }

    public PrimaryInvestigator getPerformedByPi()       { return performedByPi; }
    public void setPerformedByPi(PrimaryInvestigator v) { this.performedByPi = v; }

    public OffsetDateTime getCreatedAt()                { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)         { this.createdAt = v; }

    public OffsetDateTime getUpdatedAt()                { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime v)         { this.updatedAt = v; }
}
