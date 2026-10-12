package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.TeamMemberRole;

import java.time.OffsetDateTime;
import java.util.UUID;

public class StudyCreationDelegateResponse {

    private UUID trialId;
    private UUID memberId;
    private String memberName;
    private String email;
    private TeamMemberRole role;
    private boolean canCreateStudy;
    private OffsetDateTime grantedAt;
    private String grantedBy;

    public UUID getTrialId()                        { return trialId; }
    public void setTrialId(UUID trialId)            { this.trialId = trialId; }

    public UUID getMemberId()                       { return memberId; }
    public void setMemberId(UUID memberId)          { this.memberId = memberId; }

    public String getMemberName()                   { return memberName; }
    public void setMemberName(String memberName)    { this.memberName = memberName; }

    public String getEmail()                        { return email; }
    public void setEmail(String email)              { this.email = email; }

    public TeamMemberRole getRole()                 { return role; }
    public void setRole(TeamMemberRole role)        { this.role = role; }

    public boolean isCanCreateStudy()                       { return canCreateStudy; }
    public void setCanCreateStudy(boolean canCreateStudy)   { this.canCreateStudy = canCreateStudy; }

    public OffsetDateTime getGrantedAt()            { return grantedAt; }
    public void setGrantedAt(OffsetDateTime v)      { this.grantedAt = v; }

    public String getGrantedBy()                    { return grantedBy; }
    public void setGrantedBy(String v)              { this.grantedBy = v; }
}
