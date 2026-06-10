package com.study.management.domain.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
public class AuditLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long auditId;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "entity_id")
    private UUID entityId;

    @Column(name = "action")
    private String action;

    @Lob
    @Column(name = "old_values")
    private String oldValues;

    @Lob
    @Column(name = "new_values")
    private String newValues;

    @Lob
    @Column(name = "changed_fields")
    private String changedFields;

    @Column(name = "performed_by")
    private UUID performedBy;

    @Column(name = "performed_by_role")
    private String performedByRole;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "reason")
    private String reason;

    @Column(name = "electronic_signature")
    private String electronicSignature;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }

    // ── Getters & Setters ──────────────────────────────────────────────

    public Long getAuditId()                            { return auditId; }
    public void setAuditId(Long v)                     { this.auditId = v; }

    public String getEntityType()                       { return entityType; }
    public void setEntityType(String v)                { this.entityType = v; }

    public UUID getEntityId()                           { return entityId; }
    public void setEntityId(UUID v)                    { this.entityId = v; }

    public String getAction()                           { return action; }
    public void setAction(String action)               { this.action = action; }

    public String getOldValues()                        { return oldValues; }
    public void setOldValues(String v)                 { this.oldValues = v; }

    public String getNewValues()                        { return newValues; }
    public void setNewValues(String v)                 { this.newValues = v; }

    public String getChangedFields()                    { return changedFields; }
    public void setChangedFields(String v)             { this.changedFields = v; }

    public UUID getPerformedBy()                        { return performedBy; }
    public void setPerformedBy(UUID v)                 { this.performedBy = v; }

    public String getPerformedByRole()                  { return performedByRole; }
    public void setPerformedByRole(String v)           { this.performedByRole = v; }

    public String getIpAddress()                        { return ipAddress; }
    public void setIpAddress(String v)                 { this.ipAddress = v; }

    public String getUserAgent()                        { return userAgent; }
    public void setUserAgent(String v)                 { this.userAgent = v; }

    public String getReason()                           { return reason; }
    public void setReason(String reason)               { this.reason = reason; }

    public String getElectronicSignature()              { return electronicSignature; }
    public void setElectronicSignature(String v)       { this.electronicSignature = v; }

    public OffsetDateTime getCreatedAt()                { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)         { this.createdAt = v; }
}
