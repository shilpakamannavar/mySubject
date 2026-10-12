package com.study.management.infrastructure.web.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class StudyTeamCreateResponse {

    private UUID   trialId;
    private String message;
    private String studyName;
    private String primaryInvestigatorEmail;
    private String primaryInvestigatorName;

    private List<StudyTeamMemberResponse> coInvestigators;
    private List<StudyTeamMemberResponse> crcs;
    private List<StudyTeamMemberResponse> cros;
    private List<StudyTeamMemberResponse> hospitals;

    private String         createdByUserId;
    private String         createdByRole;
    private OffsetDateTime createdAt;

    public UUID getTrialId()                                           { return trialId; }
    public void setTrialId(UUID trialId)                               { this.trialId = trialId; }

    public String getMessage()                                         { return message; }
    public void setMessage(String message)                             { this.message = message; }

    public String getStudyName()                                       { return studyName; }
    public void setStudyName(String studyName)                         { this.studyName = studyName; }

    public String getPrimaryInvestigatorEmail()                        { return primaryInvestigatorEmail; }
    public void setPrimaryInvestigatorEmail(String v)                  { this.primaryInvestigatorEmail = v; }

    public String getPrimaryInvestigatorName()                         { return primaryInvestigatorName; }
    public void setPrimaryInvestigatorName(String v)                   { this.primaryInvestigatorName = v; }

    public List<StudyTeamMemberResponse> getCoInvestigators()          { return coInvestigators; }
    public void setCoInvestigators(List<StudyTeamMemberResponse> list) { this.coInvestigators = list; }

    public List<StudyTeamMemberResponse> getCrcs()                     { return crcs; }
    public void setCrcs(List<StudyTeamMemberResponse> list)            { this.crcs = list; }

    public List<StudyTeamMemberResponse> getCros()                     { return cros; }
    public void setCros(List<StudyTeamMemberResponse> list)            { this.cros = list; }

    public List<StudyTeamMemberResponse> getHospitals()                { return hospitals; }
    public void setHospitals(List<StudyTeamMemberResponse> list)       { this.hospitals = list; }

    public String getCreatedByUserId()                                 { return createdByUserId; }
    public void setCreatedByUserId(String v)                           { this.createdByUserId = v; }

    public String getCreatedByRole()                                   { return createdByRole; }
    public void setCreatedByRole(String v)                             { this.createdByRole = v; }

    public OffsetDateTime getCreatedAt()                               { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)                         { this.createdAt = v; }
}
