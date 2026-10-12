package com.study.management.domain.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "contract_research_organizations")
public class ContractResearchOrganization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cro_id")
    private UUID croId;

    @Column(name = "cro_number")
    private String croNumber;

    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "registration_number")
    private String registrationNumber;

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

    public UUID getCroId()                               { return croId; }
    public void setCroId(UUID croId)                    { this.croId = croId; }

    public String getCroNumber()                         { return croNumber; }
    public void setCroNumber(String croNumber)          { this.croNumber = croNumber; }

    public String getOrganizationName()                  { return organizationName; }
    public void setOrganizationName(String v)           { this.organizationName = v; }

    public String getContactPerson()                     { return contactPerson; }
    public void setContactPerson(String v)              { this.contactPerson = v; }

    public String getEmail()                             { return email; }
    public void setEmail(String v)                      { this.email = v; }

    public String getPhone()                             { return phone; }
    public void setPhone(String v)                      { this.phone = v; }

    public String getAddress()                           { return address; }
    public void setAddress(String v)                    { this.address = v; }

    public String getRegistrationNumber()                { return registrationNumber; }
    public void setRegistrationNumber(String v)         { this.registrationNumber = v; }

    public MemberStatus getStatus()                      { return status; }
    public void setStatus(MemberStatus status)          { this.status = status; }

    public OffsetDateTime getCreatedAt()                 { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)          { this.createdAt = v; }

    public OffsetDateTime getUpdatedAt()                 { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime v)          { this.updatedAt = v; }
}
