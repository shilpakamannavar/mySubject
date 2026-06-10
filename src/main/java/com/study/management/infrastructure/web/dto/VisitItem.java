package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.VisitStatus;

import java.time.LocalDate;
import java.util.UUID;

public class VisitItem {

    private UUID visitId;
    private UUID enrollmentId;
    private UUID planVisitId;
    private Integer visitNumber;
    private String visitName;
    private LocalDate scheduledDate;
    private LocalDate actualDate;
    private LocalDate windowOpenDate;
    private LocalDate windowCloseDate;
    private VisitStatus visitStatus;
    private String displayColor;
    private String watermark;

    public UUID getVisitId()                        { return visitId; }
    public void setVisitId(UUID v)                 { this.visitId = v; }
    public UUID getEnrollmentId()                   { return enrollmentId; }
    public void setEnrollmentId(UUID v)            { this.enrollmentId = v; }
    public UUID getPlanVisitId()                    { return planVisitId; }
    public void setPlanVisitId(UUID v)             { this.planVisitId = v; }
    public Integer getVisitNumber()                 { return visitNumber; }
    public void setVisitNumber(Integer v)          { this.visitNumber = v; }
    public String getVisitName()                    { return visitName; }
    public void setVisitName(String v)             { this.visitName = v; }
    public LocalDate getScheduledDate()             { return scheduledDate; }
    public void setScheduledDate(LocalDate v)      { this.scheduledDate = v; }
    public LocalDate getActualDate()                { return actualDate; }
    public void setActualDate(LocalDate v)         { this.actualDate = v; }
    public LocalDate getWindowOpenDate()            { return windowOpenDate; }
    public void setWindowOpenDate(LocalDate v)     { this.windowOpenDate = v; }
    public LocalDate getWindowCloseDate()           { return windowCloseDate; }
    public void setWindowCloseDate(LocalDate v)    { this.windowCloseDate = v; }
    public VisitStatus getVisitStatus()             { return visitStatus; }
    public void setVisitStatus(VisitStatus v)      { this.visitStatus = v; }
    public String getDisplayColor()                 { return displayColor; }
    public void setDisplayColor(String v)          { this.displayColor = v; }
    public String getWatermark()                    { return watermark; }
    public void setWatermark(String v)             { this.watermark = v; }
}
