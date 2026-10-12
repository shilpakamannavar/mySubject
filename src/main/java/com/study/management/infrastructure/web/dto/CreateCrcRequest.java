package com.study.management.infrastructure.web.dto;

public class CreateCrcRequest {

    private String firstName;
    private String lastName;
    private String title;
    private String credentials;
    private String email;
    private String phone;
    private String certificationNumber;

    public String getFirstName()                   { return firstName; }
    public void setFirstName(String v)             { this.firstName = v; }
    public String getLastName()                    { return lastName; }
    public void setLastName(String v)              { this.lastName = v; }
    public String getTitle()                       { return title; }
    public void setTitle(String v)                 { this.title = v; }
    public String getCredentials()                 { return credentials; }
    public void setCredentials(String v)           { this.credentials = v; }
    public String getEmail()                       { return email; }
    public void setEmail(String v)                 { this.email = v; }
    public String getPhone()                       { return phone; }
    public void setPhone(String v)                 { this.phone = v; }
    public String getCertificationNumber()         { return certificationNumber; }
    public void setCertificationNumber(String v)   { this.certificationNumber = v; }
}