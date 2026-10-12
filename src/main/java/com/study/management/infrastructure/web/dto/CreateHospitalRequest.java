package com.study.management.infrastructure.web.dto;

public class CreateHospitalRequest {

    private String hospitalName;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private String registrationNumber;

    public String getHospitalName()              { return hospitalName; }
    public void setHospitalName(String v)        { this.hospitalName = v; }
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