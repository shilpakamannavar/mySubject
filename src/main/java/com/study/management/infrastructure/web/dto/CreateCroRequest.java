package com.study.management.infrastructure.web.dto;

public class CreateCroRequest {

    private String organizationName;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private String registrationNumber;

    public String getOrganizationName()          { return organizationName; }
    public void setOrganizationName(String v)    { this.organizationName = v; }
    public String getContactPerson()             { return contactPerson; }
    public void setContactPerson(String v)       { this.contactPerson = v; }
    public String getEmail()                     { return email; }
    public void setEmail(String v)               { this.email = v; }
    public String getPhone()                     { return phone; }
    public void setPhone(String v)               { this.phone = v; }
    public String getAddress()                   { return address; }
    public void setAddress(String v)             { this.address = v; }
    public String getRegistrationNumber()        { return registrationNumber; }
    public void setRegistrationNumber(String v)  { this.registrationNumber = v; }
}