package com.study.management.infrastructure.web.dto;

public class CreatePiRequest {

    private String piNumber;
    private String firstName;
    private String lastName;
    private String title;
    private String credentials;
    private String specialty;
    private String email;
    private String phone;
    private String npiNumber;
    private String licenseNumber;
    private String licenseState;
    private String cvDocumentUrl;

    public String getPiNumber()                 { return piNumber; }
    public void setPiNumber(String v)          { this.piNumber = v; }
    public String getFirstName()                { return firstName; }
    public void setFirstName(String v)         { this.firstName = v; }
    public String getLastName()                 { return lastName; }
    public void setLastName(String v)          { this.lastName = v; }
    public String getTitle()                    { return title; }
    public void setTitle(String v)             { this.title = v; }
    public String getCredentials()              { return credentials; }
    public void setCredentials(String v)       { this.credentials = v; }
    public String getSpecialty()                { return specialty; }
    public void setSpecialty(String v)         { this.specialty = v; }
    public String getEmail()                    { return email; }
    public void setEmail(String v)             { this.email = v; }
    public String getPhone()                    { return phone; }
    public void setPhone(String v)             { this.phone = v; }
    public String getNpiNumber()                { return npiNumber; }
    public void setNpiNumber(String v)         { this.npiNumber = v; }
    public String getLicenseNumber()            { return licenseNumber; }
    public void setLicenseNumber(String v)     { this.licenseNumber = v; }
    public String getLicenseState()             { return licenseState; }
    public void setLicenseState(String v)      { this.licenseState = v; }
    public String getCvDocumentUrl()            { return cvDocumentUrl; }
    public void setCvDocumentUrl(String v)     { this.cvDocumentUrl = v; }
}
