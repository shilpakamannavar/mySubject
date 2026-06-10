package com.study.management.domain.port.out;

import com.study.management.domain.model.PatientEntity;
import com.study.management.domain.model.PatientVisit;
import com.study.management.domain.model.StudyEnrollment;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.VisitStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentPort {
    StudyEnrollment save(StudyEnrollment enrollment);
    Optional<StudyEnrollment> findById(UUID enrollmentId);
    boolean existsByStudyAndPatient(StudyEntity study, PatientEntity patient);
    Page<StudyEnrollment> findByStudy(StudyEntity study, Pageable pageable);
    PatientVisit saveVisit(PatientVisit visit);
    List<PatientVisit> findVisitsByEnrollment(StudyEnrollment enrollment);
    List<PatientVisit> findVisitsByEnrollmentAndStatusIn(StudyEnrollment enrollment, List<VisitStatus> statuses);
}
