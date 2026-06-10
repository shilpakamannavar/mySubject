package com.study.management.domain.port.out;

import com.study.management.domain.model.PatientVisit;
import com.study.management.domain.model.StudyEnrollment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VisitPort {
    Optional<PatientVisit> findById(UUID visitId);
    PatientVisit save(PatientVisit visit);
    List<PatientVisit> findByEnrollmentOrdered(StudyEnrollment enrollment);
}
