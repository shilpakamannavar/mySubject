package com.study.management.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "study_enrollments")
public class StudyEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "enrollment_id")
    private UUID enrollmentId;

    /** Not persisted — carries subjectId from the controller so the service can auto-resolve the patient. */
    @Transient
    private UUID subjectId;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private StudyEntity study;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private PatientEntity patient;

    @ManyToOne
    @JoinColumn(name = "study_site_id")
    private StudySiteAssignment studySite;

    @Column(name = "subject_number")
    private String subjectNumber;

    @Column(name = "screening_date")
    private LocalDate screeningDate;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "randomization_date")
    private LocalDate randomizationDate;

    @Column(name = "randomization_number")
    private String randomizationNumber;

    @Column(name = "completion_date")
    private LocalDate completionDate;

    @Column(name = "withdrawal_date")
    private LocalDate withdrawalDate;

    @Column(name = "withdrawal_reason")
    private String withdrawalReason;

    @Column(name = "informed_consent_date")
    private LocalDate informedConsentDate;

    @Column(name = "consent_version")
    private String consentVersion;

    @Column(name = "consent_document_url")
    private String consentDocumentUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "enrollment_status")
    private EnrollmentStatus enrollmentStatus;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (updatedAt == null) updatedAt = OffsetDateTime.now();
        if (enrollmentStatus == null) enrollmentStatus = EnrollmentStatus.ENROLLED;
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // ── Getters & Setters ──────────────────────────────────────────────

    public UUID getSubjectId()                          { return subjectId; }
    public void setSubjectId(UUID v)                   { this.subjectId = v; }

    public UUID getEnrollmentId()                       { return enrollmentId; }
    public void setEnrollmentId(UUID v)                { this.enrollmentId = v; }

    public StudyEntity getStudy()                       { return study; }
    public void setStudy(StudyEntity study)            { this.study = study; }

    public PatientEntity getPatient()                   { return patient; }
    public void setPatient(PatientEntity patient)      { this.patient = patient; }

    public StudySiteAssignment getStudySite()           { return studySite; }
    public void setStudySite(StudySiteAssignment v)    { this.studySite = v; }

    public String getSubjectNumber()                    { return subjectNumber; }
    public void setSubjectNumber(String v)             { this.subjectNumber = v; }

    public LocalDate getScreeningDate()                 { return screeningDate; }
    public void setScreeningDate(LocalDate v)          { this.screeningDate = v; }

    public LocalDate getEnrollmentDate()                { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate v)         { this.enrollmentDate = v; }

    public LocalDate getRandomizationDate()             { return randomizationDate; }
    public void setRandomizationDate(LocalDate v)      { this.randomizationDate = v; }

    public String getRandomizationNumber()              { return randomizationNumber; }
    public void setRandomizationNumber(String v)       { this.randomizationNumber = v; }

    public LocalDate getCompletionDate()                { return completionDate; }
    public void setCompletionDate(LocalDate v)         { this.completionDate = v; }

    public LocalDate getWithdrawalDate()                { return withdrawalDate; }
    public void setWithdrawalDate(LocalDate v)         { this.withdrawalDate = v; }

    public String getWithdrawalReason()                 { return withdrawalReason; }
    public void setWithdrawalReason(String v)          { this.withdrawalReason = v; }

    public LocalDate getInformedConsentDate()           { return informedConsentDate; }
    public void setInformedConsentDate(LocalDate v)    { this.informedConsentDate = v; }

    public String getConsentVersion()                   { return consentVersion; }
    public void setConsentVersion(String v)            { this.consentVersion = v; }

    public String getConsentDocumentUrl()               { return consentDocumentUrl; }
    public void setConsentDocumentUrl(String v)        { this.consentDocumentUrl = v; }

    public EnrollmentStatus getEnrollmentStatus()       { return enrollmentStatus; }
    public void setEnrollmentStatus(EnrollmentStatus v) { this.enrollmentStatus = v; }

    public OffsetDateTime getCreatedAt()                { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)         { this.createdAt = v; }

    public OffsetDateTime getUpdatedAt()                { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime v)         { this.updatedAt = v; }
}
