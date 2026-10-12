package com.study.management.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "study_patient")
public class StudyPatient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_number", nullable = false, unique = true)
    private String patientNumber;

    @Column(name = "patient_initial", nullable = false, length = 3)
    private String patientInitial;

    @Column(name = "mobile_number", length = 10)
    private String mobileNumber;

    @Column(name = "screening_date")
    private LocalDate screeningDate;

    @Column(name = "enrollment_randomization_date")
    private LocalDate enrollmentRandomizationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PatientStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (status == null) status = PatientStatus.INACTIVE;
    }

    public enum PatientStatus { ACTIVE, INACTIVE }

    // ── Getters & Setters ──────────────────────────────────────────────

    public Long getId()                                         { return id; }
    public void setId(Long id)                                 { this.id = id; }

    public String getPatientNumber()                            { return patientNumber; }
    public void setPatientNumber(String v)                     { this.patientNumber = v; }

    public String getPatientInitial()                           { return patientInitial; }
    public void setPatientInitial(String v)                    { this.patientInitial = v; }

    public String getMobileNumber()                             { return mobileNumber; }
    public void setMobileNumber(String v)                      { this.mobileNumber = v; }

    public LocalDate getScreeningDate()                         { return screeningDate; }
    public void setScreeningDate(LocalDate v)                  { this.screeningDate = v; }

    public LocalDate getEnrollmentRandomizationDate()           { return enrollmentRandomizationDate; }
    public void setEnrollmentRandomizationDate(LocalDate v)    { this.enrollmentRandomizationDate = v; }

    public PatientStatus getStatus()                            { return status; }
    public void setStatus(PatientStatus status)                { this.status = status; }

    public Study getStudy()                                     { return study; }
    public void setStudy(Study study)                          { this.study = study; }

    public OffsetDateTime getCreatedAt()                        { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)                 { this.createdAt = v; }
}
