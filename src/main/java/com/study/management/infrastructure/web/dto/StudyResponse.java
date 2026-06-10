package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.ClinicalStudyStatus;
import com.study.management.domain.model.StudyPhase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class StudyResponse {

    private UUID studyId;
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
    private BigDecimal calculatedRatePerService;
    private BigDecimal totalCost;
    private ClinicalStudyStatus status;
    private OffsetDateTime activatedAt;
    private OffsetDateTime disabledAt;
    private String disabledReason;
    private UUID createdByPiId;
    private String createdByPiName;
    private String message;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public UUID getStudyId()                        { return studyId; }
    public void setStudyId(UUID v)                 { this.studyId = v; }
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
    public BigDecimal getCalculatedRatePerService() { return calculatedRatePerService; }
    public void setCalculatedRatePerService(BigDecimal v) { this.calculatedRatePerService = v; }
    public BigDecimal getTotalCost()                { return totalCost; }
    public void setTotalCost(BigDecimal v)         { this.totalCost = v; }
    public ClinicalStudyStatus getStatus()          { return status; }
    public void setStatus(ClinicalStudyStatus v)   { this.status = v; }
    public OffsetDateTime getActivatedAt()          { return activatedAt; }
    public void setActivatedAt(OffsetDateTime v)   { this.activatedAt = v; }
    public OffsetDateTime getDisabledAt()           { return disabledAt; }
    public void setDisabledAt(OffsetDateTime v)    { this.disabledAt = v; }
    public String getDisabledReason()               { return disabledReason; }
    public void setDisabledReason(String v)        { this.disabledReason = v; }
    public UUID getCreatedByPiId()                  { return createdByPiId; }
    public void setCreatedByPiId(UUID v)           { this.createdByPiId = v; }
    public String getCreatedByPiName()              { return createdByPiName; }
    public void setCreatedByPiName(String v)       { this.createdByPiName = v; }
    public String getMessage()                      { return message; }
    public void setMessage(String v)               { this.message = v; }
    public OffsetDateTime getCreatedAt()            { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)     { this.createdAt = v; }
    public OffsetDateTime getUpdatedAt()            { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime v)     { this.updatedAt = v; }
}
