package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.PatientStudyLogStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class PatientStudyLogResponse {

    private String logId;
    private String patientNumber;
    private UUID studyId;
    private UUID enrollmentId;
    private Integer visitNumber;
    private String visitName;
    private String visitType;
    private LocalDate targetScheduledDate;
    private LocalDate windowOpenDate;
    private LocalDate windowCloseDate;
    private LocalDate actualVisitDate;
    private String patientInstruction;
    private String visitSpecificInstructions;
    private PatientStudyLogStatus visitStatus;
    private boolean isReadOnly;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public String getLogId()                                       { return logId; }
    public void setLogId(String logId)                            { this.logId = logId; }

    public String getPatientNumber()                               { return patientNumber; }
    public void setPatientNumber(String patientNumber)            { this.patientNumber = patientNumber; }

    public UUID getStudyId()                                       { return studyId; }
    public void setStudyId(UUID studyId)                          { this.studyId = studyId; }

    public UUID getEnrollmentId()                                  { return enrollmentId; }
    public void setEnrollmentId(UUID enrollmentId)                { this.enrollmentId = enrollmentId; }

    public Integer getVisitNumber()                                { return visitNumber; }
    public void setVisitNumber(Integer visitNumber)               { this.visitNumber = visitNumber; }

    public String getVisitName()                                   { return visitName; }
    public void setVisitName(String v)                            { this.visitName = v; }

    public String getVisitType()                                   { return visitType; }
    public void setVisitType(String visitType)                    { this.visitType = visitType; }

    public LocalDate getTargetScheduledDate()                      { return targetScheduledDate; }
    public void setTargetScheduledDate(LocalDate targetScheduledDate) { this.targetScheduledDate = targetScheduledDate; }

    public LocalDate getWindowOpenDate()                           { return windowOpenDate; }
    public void setWindowOpenDate(LocalDate windowOpenDate)        { this.windowOpenDate = windowOpenDate; }

    public LocalDate getWindowCloseDate()                          { return windowCloseDate; }
    public void setWindowCloseDate(LocalDate windowCloseDate)      { this.windowCloseDate = windowCloseDate; }

    public LocalDate getActualVisitDate()                          { return actualVisitDate; }
    public void setActualVisitDate(LocalDate actualVisitDate)      { this.actualVisitDate = actualVisitDate; }

    public String getPatientInstruction()                          { return patientInstruction; }
    public void setPatientInstruction(String v)                   { this.patientInstruction = v; }

    public String getVisitSpecificInstructions()                   { return visitSpecificInstructions; }
    public void setVisitSpecificInstructions(String s)             { this.visitSpecificInstructions = s; }

    public PatientStudyLogStatus getVisitStatus()                  { return visitStatus; }
    public void setVisitStatus(PatientStudyLogStatus visitStatus)  { this.visitStatus = visitStatus; }

    public boolean isReadOnly()                                    { return isReadOnly; }
    public void setReadOnly(boolean readOnly)                      { this.isReadOnly = readOnly; }

    public OffsetDateTime getCreatedAt()                           { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt)            { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt()                           { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt)            { this.updatedAt = updatedAt; }
}
