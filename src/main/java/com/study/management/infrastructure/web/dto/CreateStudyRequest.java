package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.StudyPhase;

import java.time.LocalDate;
import java.util.UUID;

public class CreateStudyRequest {

    private String studyNumber;
    private String studyName;
    private String indication;
    private StudyPhase phase;
    private String protocolNumber;
    private String sponsorName;
    private String therapeuticArea;
    private Integer noOfPatientsToEnroll;
    private Integer totalNumberOfVisits;
    private LocalDate dateFirstPatientScreening;
    private LocalDate dateFirstPatientLastVisit;
    private LocalDate dateLastPatientFirstVisit;
    private LocalDate dateLastPatientLastVisit;
    private UUID createdByPiId;

    public String getStudyNumber()                  { return studyNumber; }
    public void setStudyNumber(String v)           { this.studyNumber = v; }
    public String getStudyName()                    { return studyName; }
    public void setStudyName(String v)             { this.studyName = v; }
    public String getIndication()                   { return indication; }
    public void setIndication(String v)            { this.indication = v; }
    public StudyPhase getPhase()                    { return phase; }
    public void setPhase(StudyPhase v)             { this.phase = v; }
    public String getProtocolNumber()               { return protocolNumber; }
    public void setProtocolNumber(String v)        { this.protocolNumber = v; }
    public String getSponsorName()                  { return sponsorName; }
    public void setSponsorName(String v)           { this.sponsorName = v; }
    public String getTherapeuticArea()              { return therapeuticArea; }
    public void setTherapeuticArea(String v)       { this.therapeuticArea = v; }
    public Integer getNoOfPatientsToEnroll()        { return noOfPatientsToEnroll; }
    public void setNoOfPatientsToEnroll(Integer v) { this.noOfPatientsToEnroll = v; }
    public Integer getTotalNumberOfVisits()         { return totalNumberOfVisits; }
    public void setTotalNumberOfVisits(Integer v)  { this.totalNumberOfVisits = v; }
    public LocalDate getDateFirstPatientScreening() { return dateFirstPatientScreening; }
    public void setDateFirstPatientScreening(LocalDate v) { this.dateFirstPatientScreening = v; }
    public LocalDate getDateFirstPatientLastVisit() { return dateFirstPatientLastVisit; }
    public void setDateFirstPatientLastVisit(LocalDate v) { this.dateFirstPatientLastVisit = v; }
    public LocalDate getDateLastPatientFirstVisit() { return dateLastPatientFirstVisit; }
    public void setDateLastPatientFirstVisit(LocalDate v) { this.dateLastPatientFirstVisit = v; }
    public LocalDate getDateLastPatientLastVisit()  { return dateLastPatientLastVisit; }
    public void setDateLastPatientLastVisit(LocalDate v) { this.dateLastPatientLastVisit = v; }
    public UUID getCreatedByPiId()                  { return createdByPiId; }
    public void setCreatedByPiId(UUID v)           { this.createdByPiId = v; }
}
