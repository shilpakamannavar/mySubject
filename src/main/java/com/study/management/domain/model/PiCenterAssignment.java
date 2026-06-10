package com.study.management.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "pi_center_assignments")
public class PiCenterAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "assignment_id")
    private UUID assignmentId;

    @ManyToOne
    @JoinColumn(name = "pi_id")
    private PrimaryInvestigator primaryInvestigator;

    @ManyToOne
    @JoinColumn(name = "center_id")
    private MedicalCenter medicalCenter;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private AssignmentRole role;

    @Column(name = "is_primary_site")
    private Boolean isPrimarySite;

    @Column(name = "assignment_date")
    private LocalDate assignmentDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AssignmentStatus status;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (status == null) status = AssignmentStatus.ACTIVE;
    }

    // ── Getters & Setters ──────────────────────────────────────────────

    public UUID getAssignmentId()                       { return assignmentId; }
    public void setAssignmentId(UUID v)                { this.assignmentId = v; }

    public PrimaryInvestigator getPrimaryInvestigator() { return primaryInvestigator; }
    public void setPrimaryInvestigator(PrimaryInvestigator v) { this.primaryInvestigator = v; }

    public MedicalCenter getMedicalCenter()             { return medicalCenter; }
    public void setMedicalCenter(MedicalCenter v)      { this.medicalCenter = v; }

    public AssignmentRole getRole()                     { return role; }
    public void setRole(AssignmentRole role)           { this.role = role; }

    public Boolean getIsPrimarySite()                   { return isPrimarySite; }
    public void setIsPrimarySite(Boolean v)            { this.isPrimarySite = v; }

    public LocalDate getAssignmentDate()                { return assignmentDate; }
    public void setAssignmentDate(LocalDate v)         { this.assignmentDate = v; }

    public LocalDate getEndDate()                       { return endDate; }
    public void setEndDate(LocalDate v)                { this.endDate = v; }

    public AssignmentStatus getStatus()                 { return status; }
    public void setStatus(AssignmentStatus status)     { this.status = status; }

    public OffsetDateTime getCreatedAt()                { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)         { this.createdAt = v; }
}
