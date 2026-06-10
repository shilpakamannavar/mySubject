package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.AccessLevel;
import com.study.management.domain.model.UserRole;

import java.time.OffsetDateTime;
import java.util.UUID;

public class AccessResponse {

    private UUID accessId;
    private UUID studyId;
    private UUID userId;
    private UserRole userRole;
    private AccessLevel accessLevel;
    private OffsetDateTime grantedAt;
    private OffsetDateTime revokedAt;
    private Boolean isActive;

    public UUID getAccessId()                       { return accessId; }
    public void setAccessId(UUID v)                { this.accessId = v; }
    public UUID getStudyId()                        { return studyId; }
    public void setStudyId(UUID v)                 { this.studyId = v; }
    public UUID getUserId()                         { return userId; }
    public void setUserId(UUID v)                  { this.userId = v; }
    public UserRole getUserRole()                   { return userRole; }
    public void setUserRole(UserRole v)            { this.userRole = v; }
    public AccessLevel getAccessLevel()             { return accessLevel; }
    public void setAccessLevel(AccessLevel v)      { this.accessLevel = v; }
    public OffsetDateTime getGrantedAt()            { return grantedAt; }
    public void setGrantedAt(OffsetDateTime v)     { this.grantedAt = v; }
    public OffsetDateTime getRevokedAt()            { return revokedAt; }
    public void setRevokedAt(OffsetDateTime v)     { this.revokedAt = v; }
    public Boolean getIsActive()                    { return isActive; }
    public void setIsActive(Boolean v)             { this.isActive = v; }
}
