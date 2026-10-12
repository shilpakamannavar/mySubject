package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.StudyPhase;

import java.time.LocalDate;

public class UpdateStudyDesignRequest {

    private String    studyName;
    private String    title;
    private StudyPhase phase;
    private String    therapeuticArea;
    private Integer   noOfPatientsToEnroll;
    private Integer   totalNumberOfVisits;
    private LocalDate dateFirstPatientLastVisit;
    private LocalDate dateLastPatientFirstVisit;

    public String getStudyName()                            { return studyName; }
    public void setStudyName(String v)                      { this.studyName = v; }

    public String getTitle()                                { return title; }
    public void setTitle(String v)                          { this.title = v; }

    public StudyPhase getPhase()                            { return phase; }
    public void setPhase(StudyPhase v)                      { this.phase = v; }

    public String getTherapeuticArea()                      { return therapeuticArea; }
    public void setTherapeuticArea(String v)                { this.therapeuticArea = v; }

    public Integer getNoOfPatientsToEnroll()                { return noOfPatientsToEnroll; }
    public void setNoOfPatientsToEnroll(Integer v)          { this.noOfPatientsToEnroll = v; }

    public Integer getTotalNumberOfVisits()                 { return totalNumberOfVisits; }
    public void setTotalNumberOfVisits(Integer v)           { this.totalNumberOfVisits = v; }

    public LocalDate getDateFirstPatientLastVisit()         { return dateFirstPatientLastVisit; }
    public void setDateFirstPatientLastVisit(LocalDate v)   { this.dateFirstPatientLastVisit = v; }

    public LocalDate getDateLastPatientFirstVisit()         { return dateLastPatientFirstVisit; }
    public void setDateLastPatientFirstVisit(LocalDate v)   { this.dateLastPatientFirstVisit = v; }
}
