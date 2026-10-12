package com.study.management.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "subject_id")
    private UUID subjectId;

    @Column(name = "subject_number")
    private String subjectNumber;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "initials")
    private String initials;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender")
    private String gender;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "activation_status")
    private ActivationStatus activationStatus;

    /** Direct FK to the patients table — set by CRC/admin when they create the patient record for this subject. */
    @Column(name = "patient_id")
    private UUID patientId;

    @Column(name = "qr_token")
    private String qrToken;

    @Column(name = "qr_token_expires_at")
    private OffsetDateTime qrTokenExpiresAt;

    @Column(name = "activated_at")
    private OffsetDateTime activatedAt;

    @Column(name = "activated_by")
    private String activatedBy;

    @Column(name = "deactivated_at")
    private OffsetDateTime deactivatedAt;

    @Column(name = "deactivated_by")
    private String deactivatedBy;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (updatedAt == null) updatedAt = OffsetDateTime.now();
        if (status == null) status = MemberStatus.ACTIVE;
        if (activationStatus == null) activationStatus = ActivationStatus.INACTIVE;
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public UUID getSubjectId()                           { return subjectId; }
    public void setSubjectId(UUID subjectId)            { this.subjectId = subjectId; }

    public String getSubjectNumber()                     { return subjectNumber; }
    public void setSubjectNumber(String v)              { this.subjectNumber = v; }

    public String getFirstName()                         { return firstName; }
    public void setFirstName(String v)                  { this.firstName = v; }

    public String getLastName()                          { return lastName; }
    public void setLastName(String v)                   { this.lastName = v; }

    public String getInitials()                          { return initials; }
    public void setInitials(String v)                   { this.initials = v; }

    public LocalDate getDateOfBirth()                    { return dateOfBirth; }
    public void setDateOfBirth(LocalDate v)             { this.dateOfBirth = v; }

    public String getGender()                            { return gender; }
    public void setGender(String v)                     { this.gender = v; }

    public String getEmail()                             { return email; }
    public void setEmail(String v)                      { this.email = v; }

    public String getPhone()                             { return phone; }
    public void setPhone(String v)                      { this.phone = v; }

    public MemberStatus getStatus()                             { return status; }
    public void setStatus(MemberStatus status)                 { this.status = status; }

    public ActivationStatus getActivationStatus()              { return activationStatus; }
    public void setActivationStatus(ActivationStatus v)        { this.activationStatus = v; }

    public UUID getPatientId()                                { return patientId; }
    public void setPatientId(UUID v)                         { this.patientId = v; }

    public String getQrToken()                                 { return qrToken; }
    public void setQrToken(String v)                          { this.qrToken = v; }

    public OffsetDateTime getQrTokenExpiresAt()                { return qrTokenExpiresAt; }
    public void setQrTokenExpiresAt(OffsetDateTime v)          { this.qrTokenExpiresAt = v; }

    public OffsetDateTime getActivatedAt()                     { return activatedAt; }
    public void setActivatedAt(OffsetDateTime v)               { this.activatedAt = v; }

    public String getActivatedBy()                             { return activatedBy; }
    public void setActivatedBy(String v)                      { this.activatedBy = v; }

    public OffsetDateTime getDeactivatedAt()                   { return deactivatedAt; }
    public void setDeactivatedAt(OffsetDateTime v)             { this.deactivatedAt = v; }

    public String getDeactivatedBy()                           { return deactivatedBy; }
    public void setDeactivatedBy(String v)                    { this.deactivatedBy = v; }

    public OffsetDateTime getCreatedAt()                       { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)                 { this.createdAt = v; }

    public OffsetDateTime getUpdatedAt()                       { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime v)                 { this.updatedAt = v; }
}