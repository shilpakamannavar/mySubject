package com.study.management.domain.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "co_investigators")
public class CoInvestigator {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "co_investigator_id")
    private UUID ciId;

    @Column(name = "ci_number")
    private String ciNumber;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "title")
    private String title;

    @Column(name = "credentials")
    private String credentials;

    @Column(name = "specialty")
    private String specialty;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "npi_number")
    private String npiNumber;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(name = "license_state")
    private String licenseState;

    @Column(name = "cv_document_url")
    private String cvDocumentUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MemberStatus status;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (updatedAt == null) updatedAt = OffsetDateTime.now();
        if (status == null) status = MemberStatus.ACTIVE;
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public UUID getCiId()                                { return ciId; }
    public void setCiId(UUID ciId)                      { this.ciId = ciId; }

    public String getCiNumber()                          { return ciNumber; }
    public void setCiNumber(String ciNumber)            { this.ciNumber = ciNumber; }

    public String getFirstName()                         { return firstName; }
    public void setFirstName(String v)                  { this.firstName = v; }

    public String getLastName()                          { return lastName; }
    public void setLastName(String v)                   { this.lastName = v; }

    public String getTitle()                             { return title; }
    public void setTitle(String v)                      { this.title = v; }

    public String getCredentials()                       { return credentials; }
    public void setCredentials(String v)                { this.credentials = v; }

    public String getSpecialty()                         { return specialty; }
    public void setSpecialty(String v)                  { this.specialty = v; }

    public String getEmail()                             { return email; }
    public void setEmail(String v)                      { this.email = v; }

    public String getPhone()                             { return phone; }
    public void setPhone(String v)                      { this.phone = v; }

    public String getNpiNumber()                         { return npiNumber; }
    public void setNpiNumber(String v)                  { this.npiNumber = v; }

    public String getLicenseNumber()                     { return licenseNumber; }
    public void setLicenseNumber(String v)              { this.licenseNumber = v; }

    public String getLicenseState()                      { return licenseState; }
    public void setLicenseState(String v)               { this.licenseState = v; }

    public String getCvDocumentUrl()                     { return cvDocumentUrl; }
    public void setCvDocumentUrl(String v)              { this.cvDocumentUrl = v; }

    public MemberStatus getStatus()                      { return status; }
    public void setStatus(MemberStatus status)          { this.status = status; }

    public OffsetDateTime getCreatedAt()                 { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)          { this.createdAt = v; }

    public OffsetDateTime getUpdatedAt()                 { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime v)          { this.updatedAt = v; }
}
