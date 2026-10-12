package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.ClinicalStudyStatus;
import com.study.management.domain.model.ClinicalVisitType;
import com.study.management.domain.model.DurationUnit;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class StudyTemplateResponse {

    private UUID studyId;
    private String studyNumber;
    private String indication;
    private Integer noOfPatientsToEnroll;
    private Integer totalNumberOfVisits;
    private ClinicalStudyStatus status;
    private List<VisitTemplateItem> visits;
    private OffsetDateTime createdAt;

    public UUID getStudyId()                                { return studyId; }
    public void setStudyId(UUID v)                         { this.studyId = v; }

    public String getStudyNumber()                          { return studyNumber; }
    public void setStudyNumber(String v)                   { this.studyNumber = v; }

    public String getIndication()                           { return indication; }
    public void setIndication(String v)                    { this.indication = v; }

    public Integer getNoOfPatientsToEnroll()                { return noOfPatientsToEnroll; }
    public void setNoOfPatientsToEnroll(Integer v)         { this.noOfPatientsToEnroll = v; }

    public Integer getTotalNumberOfVisits()                 { return totalNumberOfVisits; }
    public void setTotalNumberOfVisits(Integer v)          { this.totalNumberOfVisits = v; }

    public ClinicalStudyStatus getStatus()                  { return status; }
    public void setStatus(ClinicalStudyStatus v)           { this.status = v; }

    public List<VisitTemplateItem> getVisits()              { return visits; }
    public void setVisits(List<VisitTemplateItem> v)       { this.visits = v; }

    public OffsetDateTime getCreatedAt()                    { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)             { this.createdAt = v; }

    public static class VisitTemplateItem {

        private UUID planVisitId;
        private Integer visitNumber;
        private String visitName;
        private ClinicalVisitType visitType;
        private Integer durationValue;
        private DurationUnit durationUnit;
        private Integer targetDayOffset;
        private Integer windowPeriodBefore;
        private Integer windowPeriodAfter;
        private String patientInstruction;

        public UUID getPlanVisitId()                         { return planVisitId; }
        public void setPlanVisitId(UUID v)                  { this.planVisitId = v; }

        public Integer getVisitNumber()                      { return visitNumber; }
        public void setVisitNumber(Integer v)               { this.visitNumber = v; }

        public String getVisitName()                         { return visitName; }
        public void setVisitName(String v)                  { this.visitName = v; }

        public ClinicalVisitType getVisitType()              { return visitType; }
        public void setVisitType(ClinicalVisitType v)       { this.visitType = v; }

        public Integer getDurationValue()                    { return durationValue; }
        public void setDurationValue(Integer v)             { this.durationValue = v; }

        public DurationUnit getDurationUnit()                { return durationUnit; }
        public void setDurationUnit(DurationUnit v)         { this.durationUnit = v; }

        public Integer getTargetDayOffset()                  { return targetDayOffset; }
        public void setTargetDayOffset(Integer v)           { this.targetDayOffset = v; }

        public Integer getWindowPeriodBefore()               { return windowPeriodBefore; }
        public void setWindowPeriodBefore(Integer v)        { this.windowPeriodBefore = v; }

        public Integer getWindowPeriodAfter()                { return windowPeriodAfter; }
        public void setWindowPeriodAfter(Integer v)         { this.windowPeriodAfter = v; }

        public String getPatientInstruction()                { return patientInstruction; }
        public void setPatientInstruction(String v)         { this.patientInstruction = v; }
    }
}
