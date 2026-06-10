package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.ClinicalVisitType;

import java.math.BigDecimal;

public class PlanVisitRequest {

    private Integer visitNumber;
    private String visitName;
    private ClinicalVisitType visitType;
    private String typeOfVisitOrDose;
    private Integer durationDays;
    private String durationDisplay;
    private Integer windowPeriodBefore;
    private Integer windowPeriodAfter;
    private String windowPeriodDisplay;
    private String dayVisitSpecificCompPattern;
    private Integer targetDayOffset;
    private Boolean isMandatory;
    private Boolean requiresFasting;
    private String procedures;
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
    public Integer getDurationDays()                { return durationDays; }
    public void setDurationDays(Integer v)         { this.durationDays = v; }
    public String getDurationDisplay()              { return durationDisplay; }
    public void setDurationDisplay(String v)       { this.durationDisplay = v; }
    public Integer getWindowPeriodBefore()          { return windowPeriodBefore; }
    public void setWindowPeriodBefore(Integer v)   { this.windowPeriodBefore = v; }
    public Integer getWindowPeriodAfter()           { return windowPeriodAfter; }
    public void setWindowPeriodAfter(Integer v)    { this.windowPeriodAfter = v; }
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
    public String getProcedures()                   { return procedures; }
    public void setProcedures(String v)            { this.procedures = v; }
    public BigDecimal getVisitCost()                { return visitCost; }
    public void setVisitCost(BigDecimal v)         { this.visitCost = v; }
    public Integer getSortOrder()                   { return sortOrder; }
    public void setSortOrder(Integer v)            { this.sortOrder = v; }
}
