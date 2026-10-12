package com.study.management.infrastructure.web.dto;

import java.util.List;
import java.util.UUID;

public class PiDashboardResponse {

    private UUID piId;
    private String piName;
    private String piNumber;
    private String specialty;
    private int totalStudies;
    private int activeStudies;
    private int totalPatients;
    private int totalCenters;
    private List<StudyListItem> recentStudies;
    private List<AssignmentResponse> centerAssignments;

    public UUID getPiId()                               { return piId; }
    public void setPiId(UUID v)                        { this.piId = v; }
    public String getPiName()                           { return piName; }
    public void setPiName(String v)                    { this.piName = v; }
    public String getPiNumber()                         { return piNumber; }
    public void setPiNumber(String v)                  { this.piNumber = v; }
    public String getSpecialty()                        { return specialty; }
    public void setSpecialty(String v)                 { this.specialty = v; }
    public int getTotalStudies()                        { return totalStudies; }
    public void setTotalStudies(int v)                 { this.totalStudies = v; }
    public int getActiveStudies()                       { return activeStudies; }
    public void setActiveStudies(int v)                { this.activeStudies = v; }
    public int getTotalPatients()                       { return totalPatients; }
    public void setTotalPatients(int v)                { this.totalPatients = v; }
    public int getTotalCenters()                        { return totalCenters; }
    public void setTotalCenters(int v)                 { this.totalCenters = v; }
    public List<StudyListItem> getRecentStudies()       { return recentStudies; }
    public void setRecentStudies(List<StudyListItem> v) { this.recentStudies = v; }
    public List<AssignmentResponse> getCenterAssignments() { return centerAssignments; }
    public void setCenterAssignments(List<AssignmentResponse> v) { this.centerAssignments = v; }
}
