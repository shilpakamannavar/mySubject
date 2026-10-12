package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.PrimaryInvestigator;
import com.study.management.domain.model.StudyTeam;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaStudyTeamRepository extends JpaRepository<StudyTeam, UUID> {

    @EntityGraph(attributePaths = {
        "primaryInvestigator",
        "members",
        "members.coInvestigator",
        "members.crc",
        "members.cro",
        "members.hospital"
    })
    @Override
    List<StudyTeam> findAll();

    @EntityGraph(attributePaths = {
        "primaryInvestigator",
        "members",
        "members.coInvestigator",
        "members.crc",
        "members.cro",
        "members.hospital"
    })
    @Override
    Optional<StudyTeam> findById(UUID id);

    @EntityGraph(attributePaths = {
        "primaryInvestigator",
        "members",
        "members.coInvestigator",
        "members.crc",
        "members.cro",
        "members.hospital"
    })
    List<StudyTeam> findByPrimaryInvestigator(PrimaryInvestigator pi);

    @EntityGraph(attributePaths = {
        "primaryInvestigator",
        "members",
        "members.coInvestigator",
        "members.crc",
        "members.cro",
        "members.hospital"
    })
    @Query("SELECT DISTINCT t FROM StudyTeam t JOIN t.members m JOIN m.coInvestigator ci " +
           "WHERE ci.phone = :userId OR ci.email = :userId")
    List<StudyTeam> findTeamsForCoInvestigator(@Param("userId") String userId);

    @EntityGraph(attributePaths = {
        "primaryInvestigator",
        "members",
        "members.coInvestigator",
        "members.crc",
        "members.cro",
        "members.hospital"
    })
    @Query("SELECT DISTINCT t FROM StudyTeam t JOIN t.members m JOIN m.crc c " +
           "WHERE c.phone = :userId OR c.email = :userId")
    List<StudyTeam> findTeamsForCrc(@Param("userId") String userId);

    @EntityGraph(attributePaths = {
        "primaryInvestigator",
        "members",
        "members.coInvestigator",
        "members.crc",
        "members.cro",
        "members.hospital"
    })
    @Query("SELECT DISTINCT t FROM StudyTeam t JOIN t.members m JOIN m.cro c " +
           "WHERE c.phone = :userId OR c.email = :userId")
    List<StudyTeam> findTeamsForCro(@Param("userId") String userId);

    @EntityGraph(attributePaths = {
        "primaryInvestigator",
        "members",
        "members.coInvestigator",
        "members.crc",
        "members.cro",
        "members.hospital"
    })
    @Query("SELECT DISTINCT t FROM StudyTeam t JOIN t.members m JOIN m.hospital h " +
           "WHERE h.phone = :userId OR h.email = :userId")
    List<StudyTeam> findTeamsForHospital(@Param("userId") String userId);
}