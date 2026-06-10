package com.study.management.infrastructure.web.dto;

import java.util.List;

public class PutStudyPlanRequest {

    private List<PlanVisitRequest> visits;

    public List<PlanVisitRequest> getVisits()           { return visits; }
    public void setVisits(List<PlanVisitRequest> v)    { this.visits = v; }
}
