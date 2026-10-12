package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.ActivationStatus;
import com.study.management.domain.model.MemberStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class SubjectResponse {

    private UUID         subjectId;
    /** UUID of the matching patient record (same person, different table). Used for enrollment. Null if no patient record exists yet. */
    private UUID         patientId;
    private String       subjectNumber;
    private String       firstName;
    private String       lastName;
    private String       initials;
    private LocalDate    dateOfBirth;
    private String       gender;
    private String       email;
    private String       phone;
    private MemberStatus status;
    private ActivationStatus activationStatus;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private boolean confirmed;
    private String userStatus;

    public UUID getSubjectId()                     { return subjectId; }
    public void setSubjectId(UUID v)               { this.subjectId = v; }
    public UUID getPatientId()                     { return patientId; }
    public void setPatientId(UUID v)               { this.patientId = v; }
    public String getSubjectNumber()               { return subjectNumber; }
    public void setSubjectNumber(String v)         { this.subjectNumber = v; }
    public String getFirstName()                   { return firstName; }
    public void setFirstName(String v)             { this.firstName = v; }
    public String getLastName()                    { return lastName; }
    public void setLastName(String v)              { this.lastName = v; }
    public String getInitials()                    { return initials; }
    public void setInitials(String v)              { this.initials = v; }
    public LocalDate getDateOfBirth()              { return dateOfBirth; }
    public void setDateOfBirth(LocalDate v)        { this.dateOfBirth = v; }
    public String getGender()                      { return gender; }
    public void setGender(String v)                { this.gender = v; }
    public String getEmail()                       { return email; }
    public void setEmail(String v)                 { this.email = v; }
    public String getPhone()                       { return phone; }
    public void setPhone(String v)                 { this.phone = v; }
    public MemberStatus getStatus()                        { return status; }
    public void setStatus(MemberStatus v)                  { this.status = v; }
    public ActivationStatus getActivationStatus()          { return activationStatus; }
    public void setActivationStatus(ActivationStatus v)    { this.activationStatus = v; }
    public OffsetDateTime getCreatedAt()           { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)     { this.createdAt = v; }
    public OffsetDateTime getUpdatedAt()           { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime v)     { this.updatedAt = v; }
    public boolean isConfirmed()                   { return confirmed; }
    public void setConfirmed(boolean v)            { this.confirmed = v; }
    public String getUserStatus()                  { return userStatus; }
    public void setUserStatus(String v)            { this.userStatus = v; }
}