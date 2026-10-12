package com.study.management.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "A single team member (co-investigator, CRC, CRO, or hospital)")
public class StudyTeamMemberRequest {

    @Schema(description = "Cognito user ID or phone number of the member", example = "crc@study.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String userId;

    @Schema(description = "Display name", example = "Neha Joshi")
    private String memberName;

    @Schema(description = "Email address", example = "neha@study.com")
    private String email;

    @Schema(description = "Phone number", example = "+919000000004")
    private String phone;

    public String getUserId()                  { return userId; }
    public void setUserId(String userId)       { this.userId = userId; }

    public String getMemberName()              { return memberName; }
    public void setMemberName(String v)        { this.memberName = v; }

    public String getEmail()                   { return email; }
    public void setEmail(String email)         { this.email = email; }

    public String getPhone()                   { return phone; }
    public void setPhone(String phone)         { this.phone = phone; }
}
