package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.ContractType;
import com.study.management.domain.model.MpaStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class MpaResponse {

    private UUID mpaId;
    private String mpaNumber;
    private String mpaName;
    private String organizationName;
    private ContractType contractType;
    private LocalDate effectiveDate;
    private LocalDate expirationDate;
    private MpaStatus status;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private String notes;
    private long centersCount;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public UUID getMpaId()                      { return mpaId; }
    public void setMpaId(UUID v)               { this.mpaId = v; }
    public String getMpaNumber()                { return mpaNumber; }
    public void setMpaNumber(String v)         { this.mpaNumber = v; }
    public String getMpaName()                  { return mpaName; }
    public void setMpaName(String v)           { this.mpaName = v; }
    public String getOrganizationName()         { return organizationName; }
    public void setOrganizationName(String v)  { this.organizationName = v; }
    public ContractType getContractType()       { return contractType; }
    public void setContractType(ContractType v) { this.contractType = v; }
    public LocalDate getEffectiveDate()         { return effectiveDate; }
    public void setEffectiveDate(LocalDate v)  { this.effectiveDate = v; }
    public LocalDate getExpirationDate()        { return expirationDate; }
    public void setExpirationDate(LocalDate v) { this.expirationDate = v; }
    public MpaStatus getStatus()                { return status; }
    public void setStatus(MpaStatus v)         { this.status = v; }
    public String getContactName()              { return contactName; }
    public void setContactName(String v)       { this.contactName = v; }
    public String getContactEmail()             { return contactEmail; }
    public void setContactEmail(String v)      { this.contactEmail = v; }
    public String getContactPhone()             { return contactPhone; }
    public void setContactPhone(String v)      { this.contactPhone = v; }
    public String getNotes()                    { return notes; }
    public void setNotes(String v)             { this.notes = v; }
    public long getCentersCount()               { return centersCount; }
    public void setCentersCount(long v)        { this.centersCount = v; }
    public OffsetDateTime getCreatedAt()        { return createdAt; }
    public void setCreatedAt(OffsetDateTime v) { this.createdAt = v; }
    public OffsetDateTime getUpdatedAt()        { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime v) { this.updatedAt = v; }
}
