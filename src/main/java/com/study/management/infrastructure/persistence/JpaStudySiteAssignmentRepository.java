package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.PrimaryInvestigator;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.StudySiteAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface JpaStudySiteAssignmentRepository extends JpaRepository<StudySiteAssignment, UUID> {
    List<StudySiteAssignment> findByStudy(StudyEntity study);
    Optional<StudySiteAssignment> findByStudyAndPrimaryInvestigator(StudyEntity study, PrimaryInvestigator pi);
}
