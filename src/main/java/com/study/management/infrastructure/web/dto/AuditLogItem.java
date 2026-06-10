package com.study.management.infrastructure.web.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class AuditLogItem {

    private Long auditId;
    private String entityType;
    private UUID entityId;
    private String action;
    private String oldValues;
    private String newValues;
    private String changedFields;
    private UUID performedBy;
    private String performedByRole;
    private String ipAddress;
    private String reason;
    private OffsetDateTime createdAt;

    public Long getAuditId()                        { return auditId; }
    public void setAuditId(Long v)                 { this.auditId = v; }
    public String getEntityType()                   { return entityType; }
    public void setEntityType(String v)            { this.entityType = v; }
    public UUID getEntityId()                       { return entityId; }
    public void setEntityId(UUID v)                { this.entityId = v; }
    public String getAction()                       { return action; }
    public void setAction(String v)                { this.action = v; }
    public String getOldValues()                    { return oldValues; }
    public void setOldValues(String v)             { this.oldValues = v; }
    public String getNewValues()                    { return newValues; }
    public void setNewValues(String v)             { this.newValues = v; }
    public String getChangedFields()                { return changedFields; }
    public void setChangedFields(String v)         { this.changedFields = v; }
    public UUID getPerformedBy()                    { return performedBy; }
    public void setPerformedBy(UUID v)             { this.performedBy = v; }
    public String getPerformedByRole()              { return performedByRole; }
    public void setPerformedByRole(String v)       { this.performedByRole = v; }
    public String getIpAddress()                    { return ipAddress; }
    public void setIpAddress(String v)             { this.ipAddress = v; }
    public String getReason()                       { return reason; }
    public void setReason(String v)                { this.reason = v; }
    public OffsetDateTime getCreatedAt()            { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)     { this.createdAt = v; }
}
