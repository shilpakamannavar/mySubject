package com.study.management.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;


@Entity
@Table(name = "studies")
public class StudyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "study_id")
    private UUID studyId;

    @Column(name = "study_number")
    private String studyNumber;

    @Column(name = "study_name")
    private String studyName;

    @Column(name = "title")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "phase")
    private StudyPhase phase;

    @Column(name = "therapeutic_area")
    private String therapeuticArea;

    @Column(name = "indication")
    private String indication;

    @Column(name = "no_of_patients_to_enroll")
    private Integer noOfPatientsToEnroll;

    @Column(name = "total_number_of_visits")
    private Integer totalNumberOfVisits;

    @Column(name = "date_first_patient_last_visit")
    private LocalDate dateFirstPatientLastVisit;

    @Column(name = "date_last_patient_first_visit")
    private LocalDate dateLastPatientFirstVisit;

    @Column(name = "calculated_rate_per_service", precision = 18, scale = 2)
    private BigDecimal calculatedRatePerService;

    @Column(name = "total_cost", precision = 18, scale = 2)
    private BigDecimal totalCost;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ClinicalStudyStatus status;

    @Column(name = "activated_at")
    private OffsetDateTime activatedAt;

    @Column(name = "disabled_at")
    private OffsetDateTime disabledAt;

    @Column(name = "disabled_reason")
    private String disabledReason;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trial_id")
    private StudyTeam studyTeam;

    @ManyToOne
    @JoinColumn(name = "created_by_pi_id")
    private PrimaryInvestigator createdByPi;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (updatedAt == null) updatedAt = OffsetDateTime.now();
        if (status == null) status = ClinicalStudyStatus.INACTIVE;
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // ── Getters & Setters ──────────────────────────────────────────────

    public UUID getStudyId()                            { return studyId; }
    public void setStudyId(UUID studyId)               { this.studyId = studyId; }

    public String getStudyNumber()                      { return studyNumber; }
    public void setStudyNumber(String v)               { this.studyNumber = v; }

    public String getStudyName()                        { return studyName; }
    public void setStudyName(String v)                 { this.studyName = v; }

    public String getTitle()                            { return title; }
    public void setTitle(String v)                     { this.title = v; }

    public StudyPhase getPhase()                        { return phase; }
    public void setPhase(StudyPhase phase)             { this.phase = phase; }

    public String getTherapeuticArea()                  { return therapeuticArea; }
    public void setTherapeuticArea(String v)           { this.therapeuticArea = v; }

    public String getIndication()                       { return indication; }
    public void setIndication(String v)                { this.indication = v; }

    public Integer getNoOfPatientsToEnroll()            { return noOfPatientsToEnroll; }
    public void setNoOfPatientsToEnroll(Integer v)     { this.noOfPatientsToEnroll = v; }

    public Integer getTotalNumberOfVisits()             { return totalNumberOfVisits; }
    public void setTotalNumberOfVisits(Integer v)      { this.totalNumberOfVisits = v; }

    public LocalDate getDateFirstPatientLastVisit()     { return dateFirstPatientLastVisit; }
    public void setDateFirstPatientLastVisit(LocalDate v) { this.dateFirstPatientLastVisit = v; }

    public LocalDate getDateLastPatientFirstVisit()     { return dateLastPatientFirstVisit; }
    public void setDateLastPatientFirstVisit(LocalDate v) { this.dateLastPatientFirstVisit = v; }

    public BigDecimal getCalculatedRatePerService()     { return calculatedRatePerService; }
    public void setCalculatedRatePerService(BigDecimal v) { this.calculatedRatePerService = v; }

    public BigDecimal getTotalCost()                    { return totalCost; }
    public void setTotalCost(BigDecimal v)             { this.totalCost = v; }

    public ClinicalStudyStatus getStatus()              { return status; }
    public void setStatus(ClinicalStudyStatus status)  { this.status = status; }

    public OffsetDateTime getActivatedAt()              { return activatedAt; }
    public void setActivatedAt(OffsetDateTime v)       { this.activatedAt = v; }

    public OffsetDateTime getDisabledAt()               { return disabledAt; }
    public void setDisabledAt(OffsetDateTime v)        { this.disabledAt = v; }

    public String getDisabledReason()                   { return disabledReason; }
    public void setDisabledReason(String v)            { this.disabledReason = v; }

    public StudyTeam getStudyTeam()                      { return studyTeam; }
    public void setStudyTeam(StudyTeam v)               { this.studyTeam = v; }

    public PrimaryInvestigator getCreatedByPi()         { return createdByPi; }
    public void setCreatedByPi(PrimaryInvestigator v)  { this.createdByPi = v; }

    public OffsetDateTime getCreatedAt()                { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)         { this.createdAt = v; }

    public OffsetDateTime getUpdatedAt()                { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime v)         { this.updatedAt = v; }
}
