package com.study.management.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

/**
 * Auto-generated visit row — one per slot in the study schedule.
 * visitNumber is set by the system; all other columns are filled by the user.
 */
@Entity
@Table(name = "study_visit")
public class StudyVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer visitNumber; // system-generated, not editable

    @Enumerated(EnumType.STRING)
    private VisitType visitType;

    @Enumerated(EnumType.STRING)
    private DurationType durationType;

    private Double durationValue;

    private Integer durationFromLastVisitDays;
    private Integer windowMinusDays;
    private Integer windowPlusDays;

    public enum DurationType { DAYS, WEEKS, MONTH }

    @Column(length = 1000)
    private String patientInstructions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    @JsonBackReference("study-visits")
    private Study study;

    // ── Getters & Setters ──────────────────────────────────────────────

    public Long getId()                     { return id; }
    public void setId(Long id)              { this.id = id; }

    public Integer getVisitNumber()         { return visitNumber; }
    public void setVisitNumber(Integer n)   { this.visitNumber = n; }

    public VisitType getVisitType()              { return visitType; }
    public void setVisitType(VisitType visitType){ this.visitType = visitType; }

    public DurationType getDurationType()            { return durationType; }
    public void setDurationType(DurationType d)      { this.durationType = d; }

    public Double getDurationValue()                 { return durationValue; }
    public void setDurationValue(Double d)           { this.durationValue = d; }

    public Integer getDurationFromLastVisitDays()                    { return durationFromLastVisitDays; }
    public void setDurationFromLastVisitDays(Integer d)              { this.durationFromLastVisitDays = d; }

    public Integer getWindowMinusDays()              { return windowMinusDays; }
    public void setWindowMinusDays(Integer w)        { this.windowMinusDays = w; }

    public Integer getWindowPlusDays()               { return windowPlusDays; }
    public void setWindowPlusDays(Integer w)         { this.windowPlusDays = w; }

    public String getPatientInstructions()                   { return patientInstructions; }
    public void setPatientInstructions(String instructions)  { this.patientInstructions = instructions; }

    public Study getStudy()                 { return study; }
    public void setStudy(Study study)       { this.study = study; }
}