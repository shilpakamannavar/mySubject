package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.MemberStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public class HospitalResponse {

    private UUID         hospitalId;
    private String       hospitalNumber;
    private String       hospitalName;
    private String       contactPerson;
    private String       email;
    private String       phone;
    private String       address;
    private String       registrationNumber;
    private MemberStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private boolean      confirmed;
    private String       userStatus;

    public UUID getHospitalId()                    { return hospitalId; }
    public void setHospitalId(UUID v)              { this.hospitalId = v; }
    public String getHospitalNumber()              { return hospitalNumber; }
    public void setHospitalNumber(String v)        { this.hospitalNumber = v; }
    public String getHospitalName()                { return hospitalName; }
    public void setHospitalName(String v)          { this.hospitalName = v; }
    public String getContactPerson()               { return contactPerson; }
    public void setContactPerson(String v)         { this.contactPerson = v; }
    public String getEmail()                       { return email; }
    public void setEmail(String v)                 { this.email = v; }
    public String getPhone()                       { return phone; }
    public void setPhone(String v)                 { this.phone = v; }
    public String getAddress()                     { return address; }
    public void setAddress(String v)               { this.address = v; }
    public String getRegistrationNumber()          { return registrationNumber; }
    public void setRegistrationNumber(String v)    { this.registrationNumber = v; }
    public MemberStatus getStatus()                { return status; }
    public void setStatus(MemberStatus v)          { this.status = v; }
    public OffsetDateTime getCreatedAt()           { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)     { this.createdAt = v; }
    public OffsetDateTime getUpdatedAt()           { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime v)     { this.updatedAt = v; }
    public boolean isConfirmed()                   { return confirmed; }
    public void setConfirmed(boolean v)            { this.confirmed = v; }
    public String getUserStatus()                  { return userStatus; }
    public void setUserStatus(String v)            { this.userStatus = v; }
}