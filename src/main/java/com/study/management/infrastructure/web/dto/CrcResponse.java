package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.MemberStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CrcResponse {

    private UUID         crcId;
    private String       crcNumber;
    private String       firstName;
    private String       lastName;
    private String       title;
    private String       credentials;
    private String       email;
    private String       phone;
    private String       certificationNumber;
    private MemberStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private boolean confirmed;
    private String userStatus;

    public UUID getCrcId()                         { return crcId; }
    public void setCrcId(UUID v)                   { this.crcId = v; }
    public String getCrcNumber()                   { return crcNumber; }
    public void setCrcNumber(String v)             { this.crcNumber = v; }
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