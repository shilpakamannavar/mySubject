package com.study.management.infrastructure.web.dto;

import jakarta.validation.constraints.Size;
import java.util.List;

public class PutStudyPlanRequest {

    @Size(max = 20, message = "Study plan cannot exceed 20 visits")
    private List<PlanVisitRequest> visits;

    public List<PlanVisitRequest> getVisits()           { return visits; }
    public void setVisits(List<PlanVisitRequest> v)    { this.visits = v; }
}
