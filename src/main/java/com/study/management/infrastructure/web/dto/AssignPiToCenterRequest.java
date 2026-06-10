package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.AssignmentRole;

import java.time.LocalDate;
import java.util.UUID;

public class AssignPiToCenterRequest {

    private UUID centerId;
    private AssignmentRole role;
    private Boolean isPrimarySite;
    private LocalDate assignmentDate;

    public UUID getCenterId()                       { return centerId; }
    public void setCenterId(UUID v)                { this.centerId = v; }
    public AssignmentRole getRole()                 { return role; }
    public void setRole(AssignmentRole v)          { this.role = v; }
    public Boolean getIsPrimarySite()               { return isPrimarySite; }
    public void setIsPrimarySite(Boolean v)        { this.isPrimarySite = v; }
    public LocalDate getAssignmentDate()            { return assignmentDate; }
    public void setAssignmentDate(LocalDate v)     { this.assignmentDate = v; }
}
