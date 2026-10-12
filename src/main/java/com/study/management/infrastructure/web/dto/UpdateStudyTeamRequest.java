package com.study.management.infrastructure.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateStudyTeamRequest {
    /** Leave null to keep the current study name unchanged */
    private String studyName;
    /** E-mail or phone of the new PI. Leave null to keep the current PI */
    private String primaryInvestigatorEmail;
    /** Full replacement list for co-investigators. Null = leave unchanged. Empty list = remove all */
    private List<String> coInvestigatorEmails;
    /** Full replacement list for CRCs. Null = leave unchanged. Empty list = remove all */
    private List<String> crcEmails;
    /** Full replacement list for CROs. Null = leave unchanged. Empty list = remove all */
    private List<String> croEmails;
    /** Full replacement list for hospitals. Null = leave unchanged. Empty list = remove all */
    private List<String> hospitalEmails;
}
