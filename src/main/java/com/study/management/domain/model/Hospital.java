package com.study.management.domain.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "hospitals")
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "hospital_id")
    private UUID hospitalId;

    @Column(name = "hospital_number")
    private String hospitalNumber;

    @Column(name = "hospital_name")
    private String hospitalName;

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

    public UUID getHospitalId()                          { return hospitalId; }
    public void setHospitalId(UUID hospitalId)          { this.hospitalId = hospitalId; }

    public String getHospitalNumber()                    { return hospitalNumber; }
    public void setHospitalNumber(String v)             { this.hospitalNumber = v; }

    public String getHospitalName()                      { return hospitalName; }
    public void setHospitalName(String v)               { this.hospitalName = v; }

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
