package com.study.management.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "patients")
public class PatientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "patient_id")
    private UUID patientId;

    @Column(name = "patient_number")
    private String patientNumber;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "initials")
    private String initials;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private PatientGender gender;

    @Column(name = "race")
    private String race;

    @Column(name = "ethnicity")
    private String ethnicity;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "emergency_phone")
    private String emergencyPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PatientStatus status;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (updatedAt == null) updatedAt = OffsetDateTime.now();
        if (status == null) status = PatientStatus.ACTIVE;
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // ── Getters & Setters ──────────────────────────────────────────────

    public UUID getPatientId()                          { return patientId; }
    public void setPatientId(UUID v)                   { this.patientId = v; }

    public String getPatientNumber()                    { return patientNumber; }
    public void setPatientNumber(String v)             { this.patientNumber = v; }

    public String getFirstName()                        { return firstName; }
    public void setFirstName(String v)                 { this.firstName = v; }

    public String getLastName()                         { return lastName; }
    public void setLastName(String v)                  { this.lastName = v; }

    public String getInitials()                         { return initials; }
    public void setInitials(String v)                  { this.initials = v; }

    public LocalDate getDateOfBirth()                   { return dateOfBirth; }
    public void setDateOfBirth(LocalDate v)            { this.dateOfBirth = v; }

    public PatientGender getGender()                    { return gender; }
    public void setGender(PatientGender gender)        { this.gender = gender; }

    public String getRace()                             { return race; }
    public void setRace(String race)                   { this.race = race; }

    public String getEthnicity()                        { return ethnicity; }
    public void setEthnicity(String v)                 { this.ethnicity = v; }

    public String getPhone()                            { return phone; }
    public void setPhone(String phone)                 { this.phone = phone; }

    public String getEmail()                            { return email; }
    public void setEmail(String email)                 { this.email = email; }

    public String getEmergencyContact()                 { return emergencyContact; }
    public void setEmergencyContact(String v)          { this.emergencyContact = v; }

    public String getEmergencyPhone()                   { return emergencyPhone; }
    public void setEmergencyPhone(String v)            { this.emergencyPhone = v; }

    public PatientStatus getStatus()                    { return status; }
    public void setStatus(PatientStatus status)        { this.status = status; }

    public OffsetDateTime getCreatedAt()                { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)         { this.createdAt = v; }

    public OffsetDateTime getUpdatedAt()                { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime v)         { this.updatedAt = v; }
}
