package com.study.management.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "study_plan_visits")
public class StudyPlanVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "plan_visit_id")
    private UUID planVisitId;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private StudyEntity study;

    @Column(name = "visit_number")
    private Integer visitNumber;

    @Column(name = "visit_name")
    private String visitName;

    @Enumerated(EnumType.STRING)
    @Column(name = "visit_type")
    private ClinicalVisitType visitType;

    @Column(name = "type_of_visit_or_dose")
    private String typeOfVisitOrDose;

    @Column(name = "duration_value")
    private Integer durationValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration_unit")
    private DurationUnit durationUnit;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "duration_display")
    private String durationDisplay;

    @Column(name = "window_period_before")
    private Integer windowPeriodBefore;

    @Column(name = "window_period_after")
    private Integer windowPeriodAfter;

    @Column(name = "window_period_display")
    private String windowPeriodDisplay;

    @Column(name = "day_visit_specific_comp_pattern")
    private String dayVisitSpecificCompPattern;

    @Column(name = "target_day_offset")
    private Integer targetDayOffset;

    @Column(name = "is_mandatory")
    private Boolean isMandatory;

    @Column(name = "requires_fasting")
    private Boolean requiresFasting;

    @Lob
    @Column(name = "procedures")
    private String procedures;

    @Column(name = "patient_instruction", length = 2000)
    private String patientInstruction;

    @Column(name = "visit_cost", precision = 18, scale = 2)
    private BigDecimal visitCost;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

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

    public UUID getPlanVisitId()                        { return planVisitId; }
    public void setPlanVisitId(UUID v)                 { this.planVisitId = v; }

    public StudyEntity getStudy()                       { return study; }
    public void setStudy(StudyEntity study)            { this.study = study; }

    public Integer getVisitNumber()                     { return visitNumber; }
    public void setVisitNumber(Integer v)              { this.visitNumber = v; }

    public String getVisitName()                        { return visitName; }
    public void setVisitName(String v)                 { this.visitName = v; }

    public ClinicalVisitType getVisitType()             { return visitType; }
    public void setVisitType(ClinicalVisitType v)      { this.visitType = v; }

    public String getTypeOfVisitOrDose()                { return typeOfVisitOrDose; }
    public void setTypeOfVisitOrDose(String v)         { this.typeOfVisitOrDose = v; }

    public Integer getDurationValue()                   { return durationValue; }
    public void setDurationValue(Integer v)            { this.durationValue = v; }

    public DurationUnit getDurationUnit()               { return durationUnit; }
    public void setDurationUnit(DurationUnit v)        { this.durationUnit = v; }

    public Integer getDurationDays()                    { return durationDays; }
    public void setDurationDays(Integer v)             { this.durationDays = v; }

    public String getDurationDisplay()                  { return durationDisplay; }
    public void setDurationDisplay(String v)           { this.durationDisplay = v; }

    public Integer getWindowPeriodBefore()              { return windowPeriodBefore; }
    public void setWindowPeriodBefore(Integer v)       { this.windowPeriodBefore = v; }

    public Integer getWindowPeriodAfter()               { return windowPeriodAfter; }
    public void setWindowPeriodAfter(Integer v)        { this.windowPeriodAfter = v; }

    public String getWindowPeriodDisplay()              { return windowPeriodDisplay; }
    public void setWindowPeriodDisplay(String v)       { this.windowPeriodDisplay = v; }

    public String getDayVisitSpecificCompPattern()      { return dayVisitSpecificCompPattern; }
    public void setDayVisitSpecificCompPattern(String v) { this.dayVisitSpecificCompPattern = v; }

    public Integer getTargetDayOffset()                 { return targetDayOffset; }
    public void setTargetDayOffset(Integer v)          { this.targetDayOffset = v; }

    public Boolean getIsMandatory()                     { return isMandatory; }
    public void setIsMandatory(Boolean v)              { this.isMandatory = v; }

    public Boolean getRequiresFasting()                 { return requiresFasting; }
    public void setRequiresFasting(Boolean v)          { this.requiresFasting = v; }

    public String getProcedures()                       { return procedures; }
    public void setProcedures(String v)                { this.procedures = v; }

    public String getPatientInstruction()               { return patientInstruction; }
    public void setPatientInstruction(String v)        { this.patientInstruction = v; }

    public BigDecimal getVisitCost()                    { return visitCost; }
    public void setVisitCost(BigDecimal v)             { this.visitCost = v; }

    public Integer getSortOrder()                       { return sortOrder; }
    public void setSortOrder(Integer v)                { this.sortOrder = v; }

    public OffsetDateTime getCreatedAt()                { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)         { this.createdAt = v; }

    public OffsetDateTime getUpdatedAt()                { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime v)         { this.updatedAt = v; }
}
