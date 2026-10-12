package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.ClinicalStudyStatus;

public class StudyStatusPatchRequest {

    private ClinicalStudyStatus status;
    private String reason;

    public ClinicalStudyStatus getStatus()          { return status; }
    public void setStatus(ClinicalStudyStatus v)   { this.status = v; }
    public String getReason()                       { return reason; }
    public void setReason(String v)                { this.reason = v; }
}
