package com.study.management.domain.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "clinical_research_coordinators")
public class ClinicalResearchCoordinator {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "crc_id")
    private UUID crcId;

    @Column(name = "crc_number")
    private String crcNumber;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "title")
    private String title;

    @Column(name = "credentials")
    private String credentials;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "certification_number")
    private String certificationNumber;

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

    public UUID getCrcId()                               { return crcId; }
    public void setCrcId(UUID crcId)                    { this.crcId = crcId; }

    public String getCrcNumber()                         { return crcNumber; }
    public void setCrcNumber(String crcNumber)          { this.crcNumber = crcNumber; }

    public String getFirstName()                         { return firstName; }
    public void setFirstName(String v)                  { this.firstName = v; }

    public String getLastName()                          { return lastName; }
    public void setLastName(String v)                   { this.lastName = v; }

    public String getTitle()                             { return title; }
    public void setTitle(String v)                      { this.title = v; }

    public String getCredentials()                       { return credentials; }
    public void setCredentials(String v)                { this.credentials = v; }

    public String getEmail()                             { return email; }
    public void setEmail(String v)                      { this.email = v; }

    public String getPhone()                             { return phone; }
    public void setPhone(String v)                      { this.phone = v; }

    public String getCertificationNumber()               { return certificationNumber; }
    public void setCertificationNumber(String v)        { this.certificationNumber = v; }

    public MemberStatus getStatus()                      { return status; }
    public void setStatus(MemberStatus status)          { this.status = status; }

    public OffsetDateTime getCreatedAt()                 { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)          { this.createdAt = v; }

    public OffsetDateTime getUpdatedAt()                 { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime v)          { this.updatedAt = v; }
}
