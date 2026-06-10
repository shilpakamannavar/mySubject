package com.study.management.infrastructure.web.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class CompleteVisitRequest {

    private LocalDate actualDate;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private Integer durationMinutes;
    private Boolean vitalsRecorded;
    private Boolean adverseEvents;
    private Boolean labSamplesTaken;
    private String proceduresDone;
    private String investigatorNotes;
    private String coordinatorNotes;
    private String patientReported;
    private UUID performedByPiId;

    public LocalDate getActualDate()                { return actualDate; }
    public void setActualDate(LocalDate v)         { this.actualDate = v; }
    public LocalTime getCheckInTime()               { return checkInTime; }
    public void setCheckInTime(LocalTime v)        { this.checkInTime = v; }
    public LocalTime getCheckOutTime()              { return checkOutTime; }
    public void setCheckOutTime(LocalTime v)       { this.checkOutTime = v; }
    public Integer getDurationMinutes()             { return durationMinutes; }
    public void setDurationMinutes(Integer v)      { this.durationMinutes = v; }
    public Boolean getVitalsRecorded()              { return vitalsRecorded; }
    public void setVitalsRecorded(Boolean v)       { this.vitalsRecorded = v; }
    public Boolean getAdverseEvents()               { return adverseEvents; }
    public void setAdverseEvents(Boolean v)        { this.adverseEvents = v; }
    public Boolean getLabSamplesTaken()             { return labSamplesTaken; }
    public void setLabSamplesTaken(Boolean v)      { this.labSamplesTaken = v; }
    public String getProceduresDone()               { return proceduresDone; }
    public void setProceduresDone(String v)        { this.proceduresDone = v; }
    public String getInvestigatorNotes()            { return investigatorNotes; }
    public void setInvestigatorNotes(String v)     { this.investigatorNotes = v; }
    public String getCoordinatorNotes()             { return coordinatorNotes; }
    public void setCoordinatorNotes(String v)      { this.coordinatorNotes = v; }
    public String getPatientReported()              { return patientReported; }
    public void setPatientReported(String v)       { this.patientReported = v; }
    public UUID getPerformedByPiId()                { return performedByPiId; }
    public void setPerformedByPiId(UUID v)         { this.performedByPiId = v; }
}
