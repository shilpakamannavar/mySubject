package com.study.management.domain.port.in;

import com.study.management.domain.model.PatientStudyLog;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PatientStudyLogUseCase {
    List<PatientStudyLog> getLogsByEnrollment(UUID studyId, UUID enrollmentId);
    List<PatientStudyLog> getLogsByStudy(UUID studyId);
    PatientStudyLog recordActualVisit(String logId, LocalDate actualDate);
}
