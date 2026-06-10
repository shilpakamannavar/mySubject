package com.study.management.infrastructure.web.dto;

import java.util.List;
import java.util.UUID;

public class VisitScheduleResponse {

    private UUID enrollmentId;
    private UUID studyId;
    private String studyName;
    private String patientNumber;
    private String subjectNumber;
    private List<VisitItem> visits;

    public UUID getEnrollmentId()                   { return enrollmentId; }
    public void setEnrollmentId(UUID v)            { this.enrollmentId = v; }
    public UUID getStudyId()                        { return studyId; }
    public void setStudyId(UUID v)                 { this.studyId = v; }
    public String getStudyName()                    { return studyName; }
    public void setStudyName(String v)             { this.studyName = v; }
    public String getPatientNumber()                { return patientNumber; }
    public void setPatientNumber(String v)         { this.patientNumber = v; }
    public String getSubjectNumber()                { return subjectNumber; }
    public void setSubjectNumber(String v)         { this.subjectNumber = v; }
    public List<VisitItem> getVisits()              { return visits; }
    public void setVisits(List<VisitItem> v)       { this.visits = v; }
}
