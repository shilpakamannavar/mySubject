package com.study.management.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "medical_centers")
public class MedicalCenter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "center_id")
    private UUID centerId;

    @ManyToOne
    @JoinColumn(name = "mpa_id")
    private Mpa mpa;

    @Column(name = "center_code")
    private String centerCode;

    @Column(name = "center_name")
    private String centerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "center_type")
    private CenterType centerType;

    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "country")
    private String country;

    @Column(name = "phone")
    private String phone;

    @Column(name = "fax")
    private String fax;

    @Column(name = "email")
    private String email;

    @Column(name = "irb_name")
    private String irbName;

    @Column(name = "irb_approval_number")
    private String irbApprovalNumber;

    @Column(name = "irb_approval_date")
    private LocalDate irbApprovalDate;

    @Column(name = "irb_expiry_date")
    private LocalDate irbExpiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CenterStatus status;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (updatedAt == null) updatedAt = OffsetDateTime.now();
        if (status == null) status = CenterStatus.ACTIVE;
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // ── Getters & Setters ──────────────────────────────────────────────

    public UUID getCenterId()                           { return centerId; }
    public void setCenterId(UUID centerId)             { this.centerId = centerId; }

    public Mpa getMpa()                                 { return mpa; }
    public void setMpa(Mpa mpa)                        { this.mpa = mpa; }

    public String getCenterCode()                       { return centerCode; }
    public void setCenterCode(String v)                { this.centerCode = v; }

    public String getCenterName()                       { return centerName; }
    public void setCenterName(String v)                { this.centerName = v; }

    public CenterType getCenterType()                   { return centerType; }
    public void setCenterType(CenterType v)            { this.centerType = v; }

    public String getAddressLine1()                     { return addressLine1; }
    public void setAddressLine1(String v)              { this.addressLine1 = v; }

    public String getAddressLine2()                     { return addressLine2; }
    public void setAddressLine2(String v)              { this.addressLine2 = v; }

    public String getCity()                             { return city; }
    public void setCity(String city)                   { this.city = city; }

    public String getState()                            { return state; }
    public void setState(String state)                 { this.state = state; }

    public String getZipCode()                          { return zipCode; }
    public void setZipCode(String v)                   { this.zipCode = v; }

    public String getCountry()                          { return country; }
    public void setCountry(String country)             { this.country = country; }

    public String getPhone()                            { return phone; }
    public void setPhone(String phone)                 { this.phone = phone; }

    public String getFax()                              { return fax; }
    public void setFax(String fax)                     { this.fax = fax; }

    public String getEmail()                            { return email; }
    public void setEmail(String email)                 { this.email = email; }

    public String getIrbName()                          { return irbName; }
    public void setIrbName(String v)                   { this.irbName = v; }

    public String getIrbApprovalNumber()                { return irbApprovalNumber; }
    public void setIrbApprovalNumber(String v)         { this.irbApprovalNumber = v; }

    public LocalDate getIrbApprovalDate()               { return irbApprovalDate; }
    public void setIrbApprovalDate(LocalDate v)        { this.irbApprovalDate = v; }

    public LocalDate getIrbExpiryDate()                 { return irbExpiryDate; }
    public void setIrbExpiryDate(LocalDate v)          { this.irbExpiryDate = v; }

    public CenterStatus getStatus()                     { return status; }
    public void setStatus(CenterStatus status)         { this.status = status; }

    public OffsetDateTime getCreatedAt()                { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)         { this.createdAt = v; }

    public OffsetDateTime getUpdatedAt()                { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime v)         { this.updatedAt = v; }
}
