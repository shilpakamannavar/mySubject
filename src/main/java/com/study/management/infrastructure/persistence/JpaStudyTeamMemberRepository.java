package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.StudyTeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JpaStudyTeamMemberRepository extends JpaRepository<StudyTeamMember, UUID> {

    @Query("SELECT m FROM StudyTeamMember m WHERE m.phone = :phone OR m.email = :phone")
    List<StudyTeamMember> findByPhoneOrEmail(@Param("phone") String phone);
}
