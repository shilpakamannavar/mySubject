package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.AssignmentRole;
import com.study.management.domain.model.AssignmentStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class AssignmentResponse {

    private UUID assignmentId;
    private UUID piId;
    private String piName;
    private UUID centerId;
    private String centerName;
    private AssignmentRole role;
    private Boolean isPrimarySite;
    private LocalDate assignmentDate;
    private LocalDate endDate;
    private AssignmentStatus status;
    private OffsetDateTime createdAt;

    public UUID getAssignmentId()                   { return assignmentId; }
    public void setAssignmentId(UUID v)            { this.assignmentId = v; }
    public UUID getPiId()                           { return piId; }
    public void setPiId(UUID v)                    { this.piId = v; }
    public String getPiName()                       { return piName; }
    public void setPiName(String v)                { this.piName = v; }
    public UUID getCenterId()                       { return centerId; }
    public void setCenterId(UUID v)                { this.centerId = v; }
    public String getCenterName()                   { return centerName; }
    public void setCenterName(String v)            { this.centerName = v; }
    public AssignmentRole getRole()                 { return role; }
    public void setRole(AssignmentRole v)          { this.role = v; }
    public Boolean getIsPrimarySite()               { return isPrimarySite; }
    public void setIsPrimarySite(Boolean v)        { this.isPrimarySite = v; }
    public LocalDate getAssignmentDate()            { return assignmentDate; }
    public void setAssignmentDate(LocalDate v)     { this.assignmentDate = v; }
    public LocalDate getEndDate()                   { return endDate; }
    public void setEndDate(LocalDate v)            { this.endDate = v; }
    public AssignmentStatus getStatus()             { return status; }
    public void setStatus(AssignmentStatus v)      { this.status = v; }
    public OffsetDateTime getCreatedAt()            { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)     { this.createdAt = v; }
}
