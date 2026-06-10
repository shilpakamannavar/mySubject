package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.ClinicalStudyStatus;
import com.study.management.domain.model.StudyPhase;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class StudyListItem {

    private UUID studyId;
    private String studyNumber;
    private String studyName;
    private String indication;
    private StudyPhase phase;
    private String sponsorName;
    private ClinicalStudyStatus status;
    private Integer noOfPatientsToEnroll;
    private BigDecimal totalCost;
    private OffsetDateTime createdAt;

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
    public String getSponsorName()                  { return sponsorName; }
    public void setSponsorName(String v)           { this.sponsorName = v; }
    public ClinicalStudyStatus getStatus()          { return status; }
    public void setStatus(ClinicalStudyStatus v)   { this.status = v; }
    public Integer getNoOfPatientsToEnroll()        { return noOfPatientsToEnroll; }
    public void setNoOfPatientsToEnroll(Integer v) { this.noOfPatientsToEnroll = v; }
    public BigDecimal getTotalCost()                { return totalCost; }
    public void setTotalCost(BigDecimal v)         { this.totalCost = v; }
    public OffsetDateTime getCreatedAt()            { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)     { this.createdAt = v; }
}
