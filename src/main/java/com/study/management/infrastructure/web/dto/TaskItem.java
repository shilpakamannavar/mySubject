package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.VisitStatus;

import java.time.LocalDate;
import java.util.UUID;

public class TaskItem {

    private UUID visitId;
    private UUID studyId;
    private String studyNumber;
    private String studyName;
    private UUID enrollmentId;
    private String subjectNumber;
    private String patientNumber;
    private String patientInitials;
    private Integer visitNumber;
    private String visitName;
    private LocalDate scheduledDate;
    private LocalDate windowOpenDate;
    private LocalDate windowCloseDate;
    private VisitStatus visitStatus;
    private String displayColor;
    private String watermark;

    public UUID getVisitId()                        { return visitId; }
    public void setVisitId(UUID v)                  { this.visitId = v; }

    public UUID getStudyId()                        { return studyId; }
    public void setStudyId(UUID v)                  { this.studyId = v; }

    public String getStudyNumber()                  { return studyNumber; }
    public void setStudyNumber(String v)            { this.studyNumber = v; }

    public String getStudyName()                    { return studyName; }
    public void setStudyName(String v)              { this.studyName = v; }

    public UUID getEnrollmentId()                   { return enrollmentId; }
    public void setEnrollmentId(UUID v)             { this.enrollmentId = v; }

    public String getSubjectNumber()                { return subjectNumber; }
    public void setSubjectNumber(String v)          { this.subjectNumber = v; }

    public String getPatientNumber()                { return patientNumber; }
    public void setPatientNumber(String v)          { this.patientNumber = v; }

    public String getPatientInitials()              { return patientInitials; }
    public void setPatientInitials(String v)        { this.patientInitials = v; }

    public Integer getVisitNumber()                 { return visitNumber; }
    public void setVisitNumber(Integer v)           { this.visitNumber = v; }

    public String getVisitName()                    { return visitName; }
    public void setVisitName(String v)              { this.visitName = v; }

    public LocalDate getScheduledDate()             { return scheduledDate; }
    public void setScheduledDate(LocalDate v)       { this.scheduledDate = v; }

    public LocalDate getWindowOpenDate()            { return windowOpenDate; }
    public void setWindowOpenDate(LocalDate v)      { this.windowOpenDate = v; }

    public LocalDate getWindowCloseDate()           { return windowCloseDate; }
    public void setWindowCloseDate(LocalDate v)     { this.windowCloseDate = v; }

    public VisitStatus getVisitStatus()             { return visitStatus; }
    public void setVisitStatus(VisitStatus v)       { this.visitStatus = v; }

    public String getDisplayColor()                 { return displayColor; }
    public void setDisplayColor(String v)           { this.displayColor = v; }

    public String getWatermark()                    { return watermark; }
    public void setWatermark(String v)              { this.watermark = v; }
}
