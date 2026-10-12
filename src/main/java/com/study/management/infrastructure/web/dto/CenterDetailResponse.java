package com.study.management.infrastructure.web.dto;

import java.util.List;

public class CenterDetailResponse extends MedicalCenterResponse {

    private List<PiResponse> investigators;

    public List<PiResponse> getInvestigators()          { return investigators; }
    public void setInvestigators(List<PiResponse> v)   { this.investigators = v; }
}
