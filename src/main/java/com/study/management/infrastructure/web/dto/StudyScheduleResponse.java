package com.study.management.infrastructure.web.dto;

import java.util.List;
import java.util.UUID;

public class StudyScheduleResponse {

    private UUID studyId;
    private String studyName;
    private String studyNumber;
    private int totalSubjectsEnrolled;
    private List<VisitScheduleResponse> subjectSchedules;

    public UUID getStudyId()                                     { return studyId; }
    public void setStudyId(UUID v)                              { this.studyId = v; }
    public String getStudyName()                                 { return studyName; }
    public void setStudyName(String v)                          { this.studyName = v; }
    public String getStudyNumber()                               { return studyNumber; }
    public void setStudyNumber(String v)                        { this.studyNumber = v; }
    public int getTotalSubjectsEnrolled()                        { return totalSubjectsEnrolled; }
    public void setTotalSubjectsEnrolled(int v)                 { this.totalSubjectsEnrolled = v; }
    public List<VisitScheduleResponse> getSubjectSchedules()     { return subjectSchedules; }
    public void setSubjectSchedules(List<VisitScheduleResponse> v) { this.subjectSchedules = v; }
}