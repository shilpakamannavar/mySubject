package com.study.management.infrastructure.web.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class StudyPlanResponse {

    private UUID studyId;
    private String studyName;
    private BigDecimal calculatedRatePerService;
    private BigDecimal totalCost;
    private List<PlanVisitResponse> visits;

    public UUID getStudyId()                            { return studyId; }
    public void setStudyId(UUID v)                     { this.studyId = v; }
    public String getStudyName()                        { return studyName; }
    public void setStudyName(String v)                 { this.studyName = v; }
    public BigDecimal getCalculatedRatePerService()     { return calculatedRatePerService; }
    public void setCalculatedRatePerService(BigDecimal v) { this.calculatedRatePerService = v; }
    public BigDecimal getTotalCost()                    { return totalCost; }
    public void setTotalCost(BigDecimal v)             { this.totalCost = v; }
    public List<PlanVisitResponse> getVisits()          { return visits; }
    public void setVisits(List<PlanVisitResponse> v)   { this.visits = v; }
}
