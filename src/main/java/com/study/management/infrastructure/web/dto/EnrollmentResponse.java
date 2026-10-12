package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.EnrollmentStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class EnrollmentResponse {

    private UUID enrollmentId;
    private UUID studyId;
    private String studyName;
    private UUID patientId;
    private String patientNumber;
    private String patientName;
    private UUID studySiteId;
    private String subjectNumber;
    private LocalDate screeningDate;
    private LocalDate enrollmentDate;
    private LocalDate randomizationDate;
    private String randomizationNumber;
    private LocalDate completionDate;
    private LocalDate withdrawalDate;
    private String withdrawalReason;
    private LocalDate informedConsentDate;
    private String consentVersion;
    private EnrollmentStatus enrollmentStatus;
    private List<VisitItem> generatedVisits;
    private List<PatientStudyLogResponse> studyPlan;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public UUID getEnrollmentId()                   { return enrollmentId; }
    public void setEnrollmentId(UUID v)            { this.enrollmentId = v; }
    public UUID getStudyId()                        { return studyId; }
    public void setStudyId(UUID v)                 { this.studyId = v; }
    public String getStudyName()                    { return studyName; }
    public void setStudyName(String v)             { this.studyName = v; }
    public UUID getPatientId()                      { return patientId; }
    public void setPatientId(UUID v)               { this.patientId = v; }
    public String getPatientNumber()                { return patientNumber; }
    public void setPatientNumber(String v)         { this.patientNumber = v; }
    public String getPatientName()                  { return patientName; }
    public void setPatientName(String v)           { this.patientName = v; }
    public UUID getStudySiteId()                    { return studySiteId; }
    public void setStudySiteId(UUID v)             { this.studySiteId = v; }
    public String getSubjectNumber()                { return subjectNumber; }
    public void setSubjectNumber(String v)         { this.subjectNumber = v; }
    public LocalDate getScreeningDate()             { return screeningDate; }
    public void setScreeningDate(LocalDate v)      { this.screeningDate = v; }
    public LocalDate getEnrollmentDate()            { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate v)     { this.enrollmentDate = v; }
    public LocalDate getRandomizationDate()         { return randomizationDate; }
    public void setRandomizationDate(LocalDate v)  { this.randomizationDate = v; }
    public String getRandomizationNumber()          { return randomizationNumber; }
    public void setRandomizationNumber(String v)   { this.randomizationNumber = v; }
    public LocalDate getCompletionDate()            { return completionDate; }
    public void setCompletionDate(LocalDate v)     { this.completionDate = v; }
    public LocalDate getWithdrawalDate()            { return withdrawalDate; }
    public void setWithdrawalDate(LocalDate v)     { this.withdrawalDate = v; }
    public String getWithdrawalReason()             { return withdrawalReason; }
    public void setWithdrawalReason(String v)      { this.withdrawalReason = v; }
    public LocalDate getInformedConsentDate()       { return informedConsentDate; }
    public void setInformedConsentDate(LocalDate v) { this.informedConsentDate = v; }
    public String getConsentVersion()               { return consentVersion; }
    public void setConsentVersion(String v)        { this.consentVersion = v; }
    public EnrollmentStatus getEnrollmentStatus()   { return enrollmentStatus; }
    public void setEnrollmentStatus(EnrollmentStatus v) { this.enrollmentStatus = v; }
    public List<VisitItem> getGeneratedVisits()     { return generatedVisits; }
    public void setGeneratedVisits(List<VisitItem> v) { this.generatedVisits = v; }
    public List<PatientStudyLogResponse> getStudyPlan() { return studyPlan; }
    public void setStudyPlan(List<PatientStudyLogResponse> v) { this.studyPlan = v; }
    public OffsetDateTime getCreatedAt()            { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)     { this.createdAt = v; }
    public OffsetDateTime getUpdatedAt()            { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime v)     { this.updatedAt = v; }
}
