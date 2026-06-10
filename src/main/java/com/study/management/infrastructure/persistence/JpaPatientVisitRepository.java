package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.PatientVisit;
import com.study.management.domain.model.StudyEnrollment;
import com.study.management.domain.model.VisitStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaPatientVisitRepository extends JpaRepository<PatientVisit, UUID> {
    List<PatientVisit> findByEnrollmentOrderByPlanVisit_VisitNumber(StudyEnrollment enrollment);
    List<PatientVisit> findByEnrollment(StudyEnrollment enrollment);
    List<PatientVisit> findByVisitStatusIn(List<VisitStatus> statuses);
}
