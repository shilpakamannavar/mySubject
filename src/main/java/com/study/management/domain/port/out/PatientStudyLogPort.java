package com.study.management.domain.port.out;

import com.study.management.domain.model.PatientStudyLog;
import com.study.management.domain.model.StudyEnrollment;
import com.study.management.domain.model.StudyEntity;

import java.util.List;
import java.util.Optional;

public interface PatientStudyLogPort {
    List<PatientStudyLog> saveAll(List<PatientStudyLog> logs);
    PatientStudyLog save(PatientStudyLog log);
    Optional<PatientStudyLog> findById(String logId);
    List<PatientStudyLog> findByEnrollmentOrderByVisitNumber(StudyEnrollment enrollment);
    List<PatientStudyLog> findByStudyOrderByVisitNumber(StudyEntity study);
    void lockReadOnlyByStudy(StudyEntity study);
}
