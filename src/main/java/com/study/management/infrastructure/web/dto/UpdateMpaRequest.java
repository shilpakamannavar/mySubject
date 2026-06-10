package com.study.management.infrastructure.web.dto;

import java.time.LocalDate;

public class UpdateMpaRequest {

    private String mpaName;
    private LocalDate expirationDate;
    private String contactName;
    private String contactEmail;
    private String notes;

    public String getMpaName()                  { return mpaName; }
    public void setMpaName(String v)           { this.mpaName = v; }
    public LocalDate getExpirationDate()        { return expirationDate; }
    public void setExpirationDate(LocalDate v) { this.expirationDate = v; }
    public String getContactName()              { return contactName; }
    public void setContactName(String v)       { this.contactName = v; }
    public String getContactEmail()             { return contactEmail; }
    public void setContactEmail(String v)      { this.contactEmail = v; }
    public String getNotes()                    { return notes; }
    public void setNotes(String v)             { this.notes = v; }
}
