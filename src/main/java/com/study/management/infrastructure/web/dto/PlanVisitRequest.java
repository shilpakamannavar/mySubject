package com.study.management.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.study.management.domain.model.ClinicalVisitType;
import com.study.management.domain.model.DurationUnit;

import java.math.BigDecimal;
import java.util.List;

public class PlanVisitRequest {

    private Integer visitNumber;
    private String visitName;
    private ClinicalVisitType visitType;
    private String typeOfVisitOrDose;
    private Integer durationValue;
    private DurationUnit durationUnit;
    private Integer durationDays;
    private String durationDisplay;
    private Integer windowPeriodBefore;
    private Integer windowPeriodAfter;
    private String windowPeriodDisplay;
    private String dayVisitSpecificCompPattern;
    private Integer targetDayOffset;
    private Boolean isMandatory;
    private Boolean requiresFasting;
    private String patientInstruction;
    private List<String> procedures;
    private BigDecimal visitCost;
    private Integer sortOrder;

    public Integer getVisitNumber()                 { return visitNumber; }
    public void setVisitNumber(Integer v)          { this.visitNumber = v; }
    public String getVisitName()                    { return visitName; }
    public void setVisitName(String v)             { this.visitName = v; }
    public ClinicalVisitType getVisitType()         { return visitType; }
    public void setVisitType(ClinicalVisitType v)  { this.visitType = v; }
    public String getTypeOfVisitOrDose()            { return typeOfVisitOrDose; }
    public void setTypeOfVisitOrDose(String v)     { this.typeOfVisitOrDose = v; }
    public Integer getDurationValue()               { return durationValue; }
    public void setDurationValue(Integer v)        { this.durationValue = v; }
    public DurationUnit getDurationUnit()           { return durationUnit; }
    public void setDurationUnit(DurationUnit v)    { this.durationUnit = v; }
    public Integer getDurationDays()               { return durationDays; }
    public void setDurationDays(Integer v)         { this.durationDays = v; }
    public String getDurationDisplay()              { return durationDisplay; }
    public void setDurationDisplay(String v)       { this.durationDisplay = v; }
    public Integer getWindowPeriodBefore()          { return windowPeriodBefore; }
    @JsonSetter("windowPeriodBefore")
    public void setWindowPeriodBefore(Object v)    { this.windowPeriodBefore = parseIntOrNull(v); }
    public Integer getWindowPeriodAfter()           { return windowPeriodAfter; }
    @JsonSetter("windowPeriodAfter")
    public void setWindowPeriodAfter(Object v)     { this.windowPeriodAfter = parseIntOrNull(v); }

    private static Integer parseIntOrNull(Object v) {
        if (v == null) return null;
        if (v instanceof Integer) return (Integer) v;
        if (v instanceof Number) return ((Number) v).intValue();
        String s = v.toString().trim();
        return s.isEmpty() ? null : Integer.parseInt(s);
    }
    public String getWindowPeriodDisplay()          { return windowPeriodDisplay; }
    public void setWindowPeriodDisplay(String v)   { this.windowPeriodDisplay = v; }
    public String getDayVisitSpecificCompPattern()  { return dayVisitSpecificCompPattern; }
    public void setDayVisitSpecificCompPattern(String v) { this.dayVisitSpecificCompPattern = v; }
    public Integer getTargetDayOffset()             { return targetDayOffset; }
    public void setTargetDayOffset(Integer v)      { this.targetDayOffset = v; }
    public Boolean getIsMandatory()                 { return isMandatory; }
    public void setIsMandatory(Boolean v)          { this.isMandatory = v; }
    public Boolean getRequiresFasting()             { return requiresFasting; }
    public void setRequiresFasting(Boolean v)      { this.requiresFasting = v; }
    public String getPatientInstruction()           { return patientInstruction; }
    public void setPatientInstruction(String v)    { this.patientInstruction = v; }
    public List<String> getProcedures()             { return procedures; }
    public void setProcedures(List<String> v)      { this.procedures = v; }
    public BigDecimal getVisitCost()                { return visitCost; }
    public void setVisitCost(BigDecimal v)         { this.visitCost = v; }
    public Integer getSortOrder()                   { return sortOrder; }
    public void setSortOrder(Integer v)            { this.sortOrder = v; }
}
