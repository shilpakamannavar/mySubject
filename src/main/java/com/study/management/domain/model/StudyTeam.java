package com.study.management.domain.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "study_teams")
public class StudyTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "trial_id")
    private UUID trialId;

    @Column(name = "study_name", nullable = false)
    private String studyName;

    @Transient
    private String primaryInvestigatorEmail;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pi_id", nullable = false)
    private PrimaryInvestigator primaryInvestigator;

    @OneToMany(mappedBy = "studyTeam", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("team-members")
    private List<StudyTeamMember> members = new ArrayList<>();

    @Column(name = "created_by_user_id", nullable = false)
    private String createdByUserId;

    @Column(name = "created_by_role", nullable = false)
    private String createdByRole;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (updatedAt == null) updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public UUID getTrialId()                              { return trialId; }
    public void setTrialId(UUID trialId)                  { this.trialId = trialId; }

    public String getStudyName()                          { return studyName; }
    public void setStudyName(String studyName)            { this.studyName = studyName; }

    public String getPrimaryInvestigatorEmail()                 { return primaryInvestigatorEmail; }
    public void setPrimaryInvestigatorEmail(String email)       { this.primaryInvestigatorEmail = email; }

    public PrimaryInvestigator getPrimaryInvestigator()         { return primaryInvestigator; }
    public void setPrimaryInvestigator(PrimaryInvestigator pi)  { this.primaryInvestigator = pi; }

    public List<StudyTeamMember> getMembers()             { return members; }
    public void setMembers(List<StudyTeamMember> members) { this.members = members; }

    public String getCreatedByUserId()                    { return createdByUserId; }
    public void setCreatedByUserId(String v)              { this.createdByUserId = v; }

    public String getCreatedByRole()                      { return createdByRole; }
    public void setCreatedByRole(String v)                { this.createdByRole = v; }

    public OffsetDateTime getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)            { this.createdAt = v; }

    public OffsetDateTime getUpdatedAt()                  { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime v)            { this.updatedAt = v; }
}
