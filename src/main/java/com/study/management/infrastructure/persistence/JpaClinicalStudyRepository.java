package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.ClinicalStudyStatus;
import com.study.management.domain.model.PrimaryInvestigator;
import com.study.management.domain.model.StudyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface JpaClinicalStudyRepository extends JpaRepository<StudyEntity, UUID> {
    List<StudyEntity> findByCreatedByPi(PrimaryInvestigator pi);
    Page<StudyEntity> findByCreatedByPi(PrimaryInvestigator pi, Pageable pageable);
    Page<StudyEntity> findByCreatedByPiAndStatus(PrimaryInvestigator pi, ClinicalStudyStatus status, Pageable pageable);
    List<StudyEntity> findByStatus(ClinicalStudyStatus status);
    Optional<StudyEntity> findByStudyNumber(String studyNumber);
    boolean existsByStudyNumber(String studyNumber);
}
