package com.study.management.domain.port.out;

import com.study.management.domain.model.PatientVisit;
import com.study.management.domain.model.StudyEnrollment;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.VisitStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VisitPort {
    Optional<PatientVisit> findById(UUID visitId);
    PatientVisit save(PatientVisit visit);
    List<PatientVisit> findByEnrollmentOrdered(StudyEnrollment enrollment);
    List<PatientVisit> findByStudyOrdered(StudyEntity study);
    List<PatientVisit> findByTeamMemberPhone(String phone, LocalDate from, LocalDate to,
                                              UUID studyId, VisitStatus status);
}
