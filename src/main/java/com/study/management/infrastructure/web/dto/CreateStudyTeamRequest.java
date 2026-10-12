package com.study.management.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Request body to create a new study team")
public class CreateStudyTeamRequest {

    @Schema(description = "Name of the clinical study", example = "Phase II Oncology Trial", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String studyName;

    @Schema(description = "Phone number of the primary investigator in E.164 format (must already be registered)", example = "+919876543210", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String primaryInvestigatorEmail;

    @Schema(description = "Phone numbers of co-investigators in E.164 format (optional)")
    private List<String> coInvestigatorEmails = new ArrayList<>();

    @Schema(description = "Phone numbers of CRCs in E.164 format (optional)")
    private List<String> crcEmails = new ArrayList<>();

    @Schema(description = "Phone numbers of CROs in E.164 format (optional)")
    private List<String> croEmails = new ArrayList<>();

    @Schema(description = "Phone numbers of hospitals in E.164 format (optional)")
    private List<String> hospitalEmails = new ArrayList<>();

    public String getStudyName()                              { return studyName; }
    public void setStudyName(String studyName)                { this.studyName = studyName; }

    public String getPrimaryInvestigatorEmail()               { return primaryInvestigatorEmail; }
    public void setPrimaryInvestigatorEmail(String v)         { this.primaryInvestigatorEmail = v; }

    public List<String> getCoInvestigatorEmails()             { return coInvestigatorEmails; }
    public void setCoInvestigatorEmails(List<String> list)    { this.coInvestigatorEmails = list; }

    public List<String> getCrcEmails()                        { return crcEmails; }
    public void setCrcEmails(List<String> list)               { this.crcEmails = list; }

    public List<String> getCroEmails()                        { return croEmails; }
    public void setCroEmails(List<String> list)               { this.croEmails = list; }

    public List<String> getHospitalEmails()                   { return hospitalEmails; }
    public void setHospitalEmails(List<String> list)          { this.hospitalEmails = list; }
}
