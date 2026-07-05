package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.StudyTeam;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaStudyTeamRepository extends JpaRepository<StudyTeam, UUID> {

    @EntityGraph(attributePaths = {"primaryInvestigator", "members"})
    @Override
    List<StudyTeam> findAll();

    @EntityGraph(attributePaths = {"primaryInvestigator", "members"})
    @Override
    Optional<StudyTeam> findById(UUID id);
}
