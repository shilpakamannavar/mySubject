package com.study.management.domain.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "study_access")
public class StudyAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "access_id")
    private UUID accessId;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private StudyEntity study;

    @Column(name = "user_id")
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_level")
    private AccessLevel accessLevel;

    @Column(name = "granted_at")
    private OffsetDateTime grantedAt;

    @Column(name = "revoked_at")
    private OffsetDateTime revokedAt;

    @Column(name = "is_active")
    private Boolean isActive;

    @PrePersist
    private void onCreate() {
        if (grantedAt == null) grantedAt = OffsetDateTime.now();
        if (isActive == null) isActive = true;
    }

    // ── Getters & Setters ──────────────────────────────────────────────

    public UUID getAccessId()                           { return accessId; }
    public void setAccessId(UUID v)                    { this.accessId = v; }

    public StudyEntity getStudy()                       { return study; }
    public void setStudy(StudyEntity study)            { this.study = study; }

    public UUID getUserId()                             { return userId; }
    public void setUserId(UUID userId)                 { this.userId = userId; }

    public UserRole getUserRole()                       { return userRole; }
    public void setUserRole(UserRole userRole)         { this.userRole = userRole; }

    public AccessLevel getAccessLevel()                 { return accessLevel; }
    public void setAccessLevel(AccessLevel v)          { this.accessLevel = v; }

    public OffsetDateTime getGrantedAt()                { return grantedAt; }
    public void setGrantedAt(OffsetDateTime v)         { this.grantedAt = v; }

    public OffsetDateTime getRevokedAt()                { return revokedAt; }
    public void setRevokedAt(OffsetDateTime v)         { this.revokedAt = v; }

    public Boolean getIsActive()                        { return isActive; }
    public void setIsActive(Boolean v)                 { this.isActive = v; }
}
