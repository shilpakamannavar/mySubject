package com.study.management.infrastructure.web.dto;

import java.time.LocalDate;

public class RescheduleVisitRequest {

    private LocalDate newScheduledDate;
    private String reason;

    public LocalDate getNewScheduledDate()          { return newScheduledDate; }
    public void setNewScheduledDate(LocalDate v)   { this.newScheduledDate = v; }
    public String getReason()                       { return reason; }
    public void setReason(String v)                { this.reason = v; }
}
