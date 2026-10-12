package com.study.management.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "study_site_assignments")
public class StudySiteAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "study_site_id")
    private UUID studySiteId;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private StudyEntity study;

    @ManyToOne
    @JoinColumn(name = "center_id")
    private MedicalCenter medicalCenter;

    @ManyToOne
    @JoinColumn(name = "pi_id")
    private PrimaryInvestigator primaryInvestigator;

    @Column(name = "site_number")
    private String siteNumber;

    @Column(name = "target_enrollment")
    private Integer targetEnrollment;

    @Column(name = "actual_enrollment")
    private Integer actualEnrollment;

    @Enumerated(EnumType.STRING)
    @Column(name = "site_status")
    private SiteStatus siteStatus;

    @Column(name = "activation_date")
    private LocalDate activationDate;

    @Column(name = "closure_date")
    private LocalDate closureDate;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (updatedAt == null) updatedAt = OffsetDateTime.now();
        if (siteStatus == null) siteStatus = SiteStatus.PENDING;
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // ── Getters & Setters ──────────────────────────────────────────────

    public UUID getStudySiteId()                        { return studySiteId; }
    public void setStudySiteId(UUID v)                 { this.studySiteId = v; }

    public StudyEntity getStudy()                       { return study; }
    public void setStudy(StudyEntity study)            { this.study = study; }

    public MedicalCenter getMedicalCenter()             { return medicalCenter; }
    public void setMedicalCenter(MedicalCenter v)      { this.medicalCenter = v; }

    public PrimaryInvestigator getPrimaryInvestigator() { return primaryInvestigator; }
    public void setPrimaryInvestigator(PrimaryInvestigator v) { this.primaryInvestigator = v; }

    public String getSiteNumber()                       { return siteNumber; }
    public void setSiteNumber(String v)                { this.siteNumber = v; }

    public Integer getTargetEnrollment()                { return targetEnrollment; }
    public void setTargetEnrollment(Integer v)         { this.targetEnrollment = v; }

    public Integer getActualEnrollment()                { return actualEnrollment; }
    public void setActualEnrollment(Integer v)         { this.actualEnrollment = v; }

    public SiteStatus getSiteStatus()                   { return siteStatus; }
    public void setSiteStatus(SiteStatus v)            { this.siteStatus = v; }

    public LocalDate getActivationDate()                { return activationDate; }
    public void setActivationDate(LocalDate v)         { this.activationDate = v; }

    public LocalDate getClosureDate()                   { return closureDate; }
    public void setClosureDate(LocalDate v)            { this.closureDate = v; }

    public OffsetDateTime getCreatedAt()                { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)         { this.createdAt = v; }

    public OffsetDateTime getUpdatedAt()                { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime v)         { this.updatedAt = v; }
}
