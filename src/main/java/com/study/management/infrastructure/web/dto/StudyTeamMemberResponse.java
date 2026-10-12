package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.TeamMemberRole;

import java.time.OffsetDateTime;
import java.util.UUID;

public class StudyTeamMemberResponse {
    private UUID memberId;
    private String email;
    private String name;
    private String memberNumber;
    private TeamMemberRole role;
    private OffsetDateTime createdAt;

    public UUID getMemberId()                      { return memberId; }
    public void setMemberId(UUID memberId)         { this.memberId = memberId; }

    public String getEmail()                       { return email; }
    public void setEmail(String email)             { this.email = email; }

    public String getName()                        { return name; }
    public void setName(String name)               { this.name = name; }

    public String getMemberNumber()                { return memberNumber; }
    public void setMemberNumber(String v)          { this.memberNumber = v; }

    public TeamMemberRole getRole()                { return role; }
    public void setRole(TeamMemberRole role)       { this.role = role; }

    public OffsetDateTime getCreatedAt()           { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)     { this.createdAt = v; }

    private boolean canCreateStudy;
    private OffsetDateTime studyCreationGrantedAt;
    private String studyCreationGrantedBy;

    public boolean isCanCreateStudy()                       { return canCreateStudy; }
    public void setCanCreateStudy(boolean canCreateStudy)   { this.canCreateStudy = canCreateStudy; }

    public OffsetDateTime getStudyCreationGrantedAt()       { return studyCreationGrantedAt; }
    public void setStudyCreationGrantedAt(OffsetDateTime v) { this.studyCreationGrantedAt = v; }

    public String getStudyCreationGrantedBy()               { return studyCreationGrantedBy; }
    public void setStudyCreationGrantedBy(String v)         { this.studyCreationGrantedBy = v; }
}
