package com.study.management.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;


@Entity
@Table(name = "study_team_members")
public class StudyTeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "member_id")
    private UUID memberId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trial_id", nullable = false)
    @JsonBackReference("team-members")
    private StudyTeam studyTeam;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "member_name")
    private String memberName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private TeamMemberRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "co_investigator_id")
    private CoInvestigator coInvestigator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crc_id")
    private ClinicalResearchCoordinator crc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cro_id")
    private ContractResearchOrganization cro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @Column(name = "can_create_study", nullable = false)
    private boolean canCreateStudy = false;

    @Column(name = "study_creation_granted_at")
    private OffsetDateTime studyCreationGrantedAt;

    @Column(name = "study_creation_granted_by")
    private String studyCreationGrantedBy;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }

    public UUID getMemberId()                     { return memberId; }
    public void setMemberId(UUID memberId)        { this.memberId = memberId; }

    public StudyTeam getStudyTeam()               { return studyTeam; }
    public void setStudyTeam(StudyTeam studyTeam) { this.studyTeam = studyTeam; }

    public String getUserId()                     { return userId; }
    public void setUserId(String userId)          { this.userId = userId; }

    public String getMemberName()                 { return memberName; }
    public void setMemberName(String memberName)  { this.memberName = memberName; }

    public String getEmail()                      { return email; }
    public void setEmail(String email)            { this.email = email; }

    public String getPhone()                      { return phone; }
    public void setPhone(String phone)            { this.phone = phone; }

    public TeamMemberRole getRole()               { return role; }
    public void setRole(TeamMemberRole role)      { this.role = role; }

    public OffsetDateTime getCreatedAt()          { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)    { this.createdAt = v; }

    public CoInvestigator getCoInvestigator()                         { return coInvestigator; }
    public void setCoInvestigator(CoInvestigator coInvestigator)      { this.coInvestigator = coInvestigator; }

    public ClinicalResearchCoordinator getCrc()                       { return crc; }
    public void setCrc(ClinicalResearchCoordinator crc)               { this.crc = crc; }

    public ContractResearchOrganization getCro()                      { return cro; }
    public void setCro(ContractResearchOrganization cro)              { this.cro = cro; }

    public Hospital getHospital()                                     { return hospital; }
    public void setHospital(Hospital hospital)                        { this.hospital = hospital; }

    public boolean isCanCreateStudy()                                         { return canCreateStudy; }
    public void setCanCreateStudy(boolean canCreateStudy)                     { this.canCreateStudy = canCreateStudy; }

    public OffsetDateTime getStudyCreationGrantedAt()                         { return studyCreationGrantedAt; }
    public void setStudyCreationGrantedAt(OffsetDateTime v)                   { this.studyCreationGrantedAt = v; }

    public String getStudyCreationGrantedBy()                                 { return studyCreationGrantedBy; }
    public void setStudyCreationGrantedBy(String v)                           { this.studyCreationGrantedBy = v; }
}
