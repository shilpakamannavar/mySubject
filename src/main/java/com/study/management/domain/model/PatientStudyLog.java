package com.study.management.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "patient_study_log")
public class PatientStudyLog {

    @Id
    @Column(name = "log_id", length = 50)
    private String logId;

    @Column(name = "patient_number", nullable = false, length = 20)
    private String patientNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private StudyEnrollment enrollment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "study_id", nullable = false)
    private StudyEntity study;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_visit_id")
    private StudyPlanVisit planVisit;

    @Column(name = "visit_number", nullable = false)
    private Integer visitNumber;

    @Column(name = "visit_type", length = 50)
    private String visitType;

    @Column(name = "target_scheduled_date")
    private LocalDate targetScheduledDate;

    @Column(name = "window_open_date")
    private LocalDate windowOpenDate;

    @Column(name = "window_close_date")
    private LocalDate windowCloseDate;

    @Column(name = "actual_visit_date")
    private LocalDate actualVisitDate;

    @Lob
    @Column(name = "visit_specific_instructions")
    private String visitSpecificInstructions;

    @Enumerated(EnumType.STRING)
    @Column(name = "visit_status", nullable = false, length = 20)
    private PatientStudyLogStatus visitStatus = PatientStudyLogStatus.SCHEDULED;

    @Column(name = "is_read_only", nullable = false)
    private boolean isReadOnly = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
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

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public String getLogId()                              { return logId; }
    public void setLogId(String logId)                   { this.logId = logId; }

    public String getPatientNumber()                      { return patientNumber; }
    public void setPatientNumber(String patientNumber)   { this.patientNumber = patientNumber; }

    public StudyEnrollment getEnrollment()                { return enrollment; }
    public void setEnrollment(StudyEnrollment enrollment){ this.enrollment = enrollment; }

    public StudyEntity getStudy()                         { return study; }
    public void setStudy(StudyEntity study)              { this.study = study; }

    public StudyPlanVisit getPlanVisit()                  { return planVisit; }
    public void setPlanVisit(StudyPlanVisit planVisit)   { this.planVisit = planVisit; }

    public Integer getVisitNumber()                       { return visitNumber; }
    public void setVisitNumber(Integer visitNumber)      { this.visitNumber = visitNumber; }

    public String getVisitType()                          { return visitType; }
    public void setVisitType(String visitType)           { this.visitType = visitType; }

    public LocalDate getTargetScheduledDate()             { return targetScheduledDate; }
    public void setTargetScheduledDate(LocalDate d)      { this.targetScheduledDate = d; }

    public LocalDate getWindowOpenDate()                  { return windowOpenDate; }
    public void setWindowOpenDate(LocalDate d)           { this.windowOpenDate = d; }

    public LocalDate getWindowCloseDate()                 { return windowCloseDate; }
    public void setWindowCloseDate(LocalDate d)          { this.windowCloseDate = d; }

    public LocalDate getActualVisitDate()                 { return actualVisitDate; }
    public void setActualVisitDate(LocalDate d)          { this.actualVisitDate = d; }

    public String getVisitSpecificInstructions()          { return visitSpecificInstructions; }
    public void setVisitSpecificInstructions(String s)   { this.visitSpecificInstructions = s; }

    public PatientStudyLogStatus getVisitStatus()         { return visitStatus; }
    public void setVisitStatus(PatientStudyLogStatus s)  { this.visitStatus = s; }

    public boolean isReadOnly()                           { return isReadOnly; }
    public void setReadOnly(boolean readOnly)            { this.isReadOnly = readOnly; }

    public OffsetDateTime getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt)   { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt()                  { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt)   { this.updatedAt = updatedAt; }
}
