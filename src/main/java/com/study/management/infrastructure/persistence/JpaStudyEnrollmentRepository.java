package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.EnrollmentStatus;
import com.study.management.domain.model.PatientEntity;
import com.study.management.domain.model.StudyEnrollment;
import com.study.management.domain.model.StudyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaStudyEnrollmentRepository extends JpaRepository<StudyEnrollment, UUID> {
    List<StudyEnrollment> findByStudy(StudyEntity study);
    Page<StudyEnrollment> findByStudy(StudyEntity study, Pageable pageable);
    List<StudyEnrollment> findByStudyAndEnrollmentStatus(StudyEntity study, EnrollmentStatus status);
    boolean existsByStudyAndPatient(StudyEntity study, PatientEntity patient);
    List<StudyEnrollment> findByStudyAndEnrollmentStatusIn(StudyEntity study, List<EnrollmentStatus> statuses);
    List<StudyEnrollment> findByPatient(PatientEntity patient);
}
