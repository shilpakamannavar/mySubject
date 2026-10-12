package com.study.management.infrastructure.web.dto;

import java.time.LocalDate;
import java.util.UUID;

public class EnrollPatientRequest {

    /**
     * Send either subjectId (preferred — backend resolves/creates the patient record automatically)
     * or patientId (legacy, when the patient record was created separately).
     * If both are supplied, subjectId takes precedence.
     */
    private UUID subjectId;
    private UUID patientId;
    private UUID studySiteId;
    private String subjectNumber;
    private LocalDate screeningDate;
    private LocalDate enrollmentDate;
    private LocalDate informedConsentDate;
    private String consentVersion;
    private String consentDocumentUrl;

    public UUID getSubjectId()                       { return subjectId; }
    public void setSubjectId(UUID v)               { this.subjectId = v; }

    public UUID getPatientId()                      { return patientId; }
    public void setPatientId(UUID v)               { this.patientId = v; }
    public UUID getStudySiteId()                    { return studySiteId; }
    public void setStudySiteId(UUID v)             { this.studySiteId = v; }
    public String getSubjectNumber()                { return subjectNumber; }
    public void setSubjectNumber(String v)         { this.subjectNumber = v; }
    public LocalDate getScreeningDate()             { return screeningDate; }
    public void setScreeningDate(LocalDate v)      { this.screeningDate = v; }
    public LocalDate getEnrollmentDate()            { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate v)     { this.enrollmentDate = v; }
    public LocalDate getInformedConsentDate()       { return informedConsentDate; }
    public void setInformedConsentDate(LocalDate v) { this.informedConsentDate = v; }
    public String getConsentVersion()               { return consentVersion; }
    public void setConsentVersion(String v)        { this.consentVersion = v; }
    public String getConsentDocumentUrl()           { return consentDocumentUrl; }
    public void setConsentDocumentUrl(String v)    { this.consentDocumentUrl = v; }
}
