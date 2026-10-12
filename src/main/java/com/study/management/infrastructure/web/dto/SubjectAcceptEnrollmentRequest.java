package com.study.management.infrastructure.web.dto;

import java.time.LocalDate;

public class SubjectAcceptEnrollmentRequest {

    /** Date the subject signed the informed consent form. Defaults to today if null. */
    private LocalDate consentDate;

    /** Version of the consent document signed (e.g. "v1.2"). Optional. */
    private String consentVersion;

    public LocalDate getConsentDate()               { return consentDate; }
    public void setConsentDate(LocalDate v)         { this.consentDate = v; }

    public String getConsentVersion()               { return consentVersion; }
    public void setConsentVersion(String v)         { this.consentVersion = v; }
}
