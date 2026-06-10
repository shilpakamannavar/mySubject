package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.CenterType;

import java.time.LocalDate;

public class CreateMedicalCenterRequest {

    private String centerCode;
    private String centerName;
    private CenterType centerType;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String phone;
    private String fax;
    private String email;
    private String irbName;
    private String irbApprovalNumber;
    private LocalDate irbApprovalDate;
    private LocalDate irbExpiryDate;

    public String getCenterCode()               { return centerCode; }
    public void setCenterCode(String v)        { this.centerCode = v; }
    public String getCenterName()               { return centerName; }
    public void setCenterName(String v)        { this.centerName = v; }
    public CenterType getCenterType()           { return centerType; }
    public void setCenterType(CenterType v)    { this.centerType = v; }
    public String getAddressLine1()             { return addressLine1; }
    public void setAddressLine1(String v)      { this.addressLine1 = v; }
    public String getAddressLine2()             { return addressLine2; }
    public void setAddressLine2(String v)      { this.addressLine2 = v; }
    public String getCity()                     { return city; }
    public void setCity(String v)              { this.city = v; }
    public String getState()                    { return state; }
    public void setState(String v)             { this.state = v; }
    public String getZipCode()                  { return zipCode; }
    public void setZipCode(String v)           { this.zipCode = v; }
    public String getCountry()                  { return country; }
    public void setCountry(String v)           { this.country = v; }
    public String getPhone()                    { return phone; }
    public void setPhone(String v)             { this.phone = v; }
    public String getFax()                      { return fax; }
    public void setFax(String v)               { this.fax = v; }
    public String getEmail()                    { return email; }
    public void setEmail(String v)             { this.email = v; }
    public String getIrbName()                  { return irbName; }
    public void setIrbName(String v)           { this.irbName = v; }
    public String getIrbApprovalNumber()        { return irbApprovalNumber; }
    public void setIrbApprovalNumber(String v) { this.irbApprovalNumber = v; }
    public LocalDate getIrbApprovalDate()       { return irbApprovalDate; }
    public void setIrbApprovalDate(LocalDate v) { this.irbApprovalDate = v; }
    public LocalDate getIrbExpiryDate()         { return irbExpiryDate; }
    public void setIrbExpiryDate(LocalDate v)  { this.irbExpiryDate = v; }
}
