package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.PatientStudyLog;
import com.study.management.domain.model.StudyEnrollment;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.port.out.PatientStudyLogPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PatientStudyLogPersistenceAdapter implements PatientStudyLogPort {

    private final JpaPatientStudyLogRepository repository;

    public PatientStudyLogPersistenceAdapter(JpaPatientStudyLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<PatientStudyLog> saveAll(List<PatientStudyLog> logs) {
        return repository.saveAll(logs);
    }

    @Override
    public PatientStudyLog save(PatientStudyLog log) {
        return repository.save(log);
    }

    @Override
    public Optional<PatientStudyLog> findById(String logId) {
        return repository.findById(logId);
    }

    @Override
    public List<PatientStudyLog> findByEnrollmentOrderByVisitNumber(StudyEnrollment enrollment) {
        return repository.findByEnrollmentOrderByVisitNumber(enrollment);
    }

    @Override
    public List<PatientStudyLog> findByStudyOrderByVisitNumber(StudyEntity study) {
        return repository.findByStudyOrderByVisitNumber(study);
    }

    @Override
    public void lockReadOnlyByStudy(StudyEntity study) {
        repository.lockReadOnlyByStudy(study);
    }
}
