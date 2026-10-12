package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.ClinicalVisitType;
import com.study.management.domain.model.DurationUnit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class StudyTemplateRequest {

    private String studyNumber;

    private String indication;

    @NotNull
    @Min(1)
    private Integer noOfPatientsToEnroll;

    @NotNull
    @Min(1)
    @Max(20)
    private Integer totalNumberOfVisits;

    @NotNull
    private UUID studyTeamId;

    @Valid
    @Size(max = 20, message = "Study plan cannot exceed 20 visits")
    private List<VisitTemplateItem> visits;

    public String getStudyNumber()                          { return studyNumber; }
    public void setStudyNumber(String v)                   { this.studyNumber = v; }

    public String getIndication()                           { return indication; }
    public void setIndication(String v)                    { this.indication = v; }

    public Integer getNoOfPatientsToEnroll()                { return noOfPatientsToEnroll; }
    public void setNoOfPatientsToEnroll(Integer v)         { this.noOfPatientsToEnroll = v; }

    public Integer getTotalNumberOfVisits()                 { return totalNumberOfVisits; }
    public void setTotalNumberOfVisits(Integer v)          { this.totalNumberOfVisits = v; }

    public UUID getStudyTeamId()                            { return studyTeamId; }
    public void setStudyTeamId(UUID v)                     { this.studyTeamId = v; }

    public List<VisitTemplateItem> getVisits()              { return visits; }
    public void setVisits(List<VisitTemplateItem> v)       { this.visits = v; }

    public static class VisitTemplateItem {

        @NotNull
        @Min(1) @Max(20)
        private Integer visitNumber;

        private String visitName;

        private ClinicalVisitType visitType;

        private Integer durationValue;

        private DurationUnit durationUnit;

        @Min(0)
        private Integer targetDayOffset;

        @Min(0)
        private Integer windowPeriodBefore;

        @Min(0)
        private Integer windowPeriodAfter;

        @Size(max = 2000)
        private String patientInstruction;

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
