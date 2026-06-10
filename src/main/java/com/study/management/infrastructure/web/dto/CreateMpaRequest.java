package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.ContractType;

import java.time.LocalDate;

public class CreateMpaRequest {

    private String mpaNumber;
    private String mpaName;
    private String organizationName;
    private ContractType contractType;
    private LocalDate effectiveDate;
    private LocalDate expirationDate;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private String notes;

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
    public String getContactName()              { return contactName; }
    public void setContactName(String v)       { this.contactName = v; }
    public String getContactEmail()             { return contactEmail; }
    public void setContactEmail(String v)      { this.contactEmail = v; }
    public String getContactPhone()             { return contactPhone; }
    public void setContactPhone(String v)      { this.contactPhone = v; }
    public String getNotes()                    { return notes; }
    public void setNotes(String v)             { this.notes = v; }
}
