package com.study.management.infrastructure.web.dto;

import java.time.LocalDate;
import java.util.UUID;

public class AssignSiteRequest {

    private UUID centerId;
    private UUID piId;
    private String siteNumber;
    private Integer targetEnrollment;
    private LocalDate activationDate;

    public UUID getCenterId()                       { return centerId; }
    public void setCenterId(UUID v)                { this.centerId = v; }
    public UUID getPiId()                           { return piId; }
    public void setPiId(UUID v)                    { this.piId = v; }
    public String getSiteNumber()                   { return siteNumber; }
    public void setSiteNumber(String v)            { this.siteNumber = v; }
    public Integer getTargetEnrollment()            { return targetEnrollment; }
    public void setTargetEnrollment(Integer v)     { this.targetEnrollment = v; }
    public LocalDate getActivationDate()            { return activationDate; }
    public void setActivationDate(LocalDate v)     { this.activationDate = v; }
}
