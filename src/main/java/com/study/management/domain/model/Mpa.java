package com.study.management.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "mpa")
public class Mpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "mpa_id")
    private UUID mpaId;

    @Column(name = "mpa_number")
    private String mpaNumber;

    @Column(name = "mpa_name")
    private String mpaName;

    @Column(name = "organization_name")
    private String organizationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type")
    private ContractType contractType;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MpaStatus status;

    @Column(name = "contact_name")
    private String contactName;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (updatedAt == null) updatedAt = OffsetDateTime.now();
        if (status == null) status = MpaStatus.DRAFT;
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // ── Getters & Setters ──────────────────────────────────────────────

    public UUID getMpaId()                              { return mpaId; }
    public void setMpaId(UUID mpaId)                   { this.mpaId = mpaId; }

    public String getMpaNumber()                        { return mpaNumber; }
    public void setMpaNumber(String mpaNumber)         { this.mpaNumber = mpaNumber; }

    public String getMpaName()                          { return mpaName; }
    public void setMpaName(String mpaName)             { this.mpaName = mpaName; }

    public String getOrganizationName()                 { return organizationName; }
    public void setOrganizationName(String v)          { this.organizationName = v; }

    public ContractType getContractType()               { return contractType; }
    public void setContractType(ContractType v)        { this.contractType = v; }

    public LocalDate getEffectiveDate()                 { return effectiveDate; }
    public void setEffectiveDate(LocalDate v)          { this.effectiveDate = v; }

    public LocalDate getExpirationDate()                { return expirationDate; }
    public void setExpirationDate(LocalDate v)         { this.expirationDate = v; }

    public MpaStatus getStatus()                        { return status; }
    public void setStatus(MpaStatus status)            { this.status = status; }

    public String getContactName()                      { return contactName; }
    public void setContactName(String v)               { this.contactName = v; }

    public String getContactEmail()                     { return contactEmail; }
    public void setContactEmail(String v)              { this.contactEmail = v; }

    public String getContactPhone()                     { return contactPhone; }
    public void setContactPhone(String v)              { this.contactPhone = v; }

    public String getNotes()                            { return notes; }
    public void setNotes(String notes)                 { this.notes = notes; }

    public OffsetDateTime getCreatedAt()                { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)         { this.createdAt = v; }

    public OffsetDateTime getUpdatedAt()                { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime v)         { this.updatedAt = v; }
}
