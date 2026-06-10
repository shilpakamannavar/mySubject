package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.Study;
import com.study.management.domain.model.StudyPatient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface JpaStudyPatientRepository extends JpaRepository<StudyPatient, Long> {

    List<StudyPatient> findByStudyOrderByPatientNumber(Study study);

    Optional<StudyPatient> findByStudyAndPatientNumber(Study study, String patientNumber);

    int countByStudy(Study study);
}
