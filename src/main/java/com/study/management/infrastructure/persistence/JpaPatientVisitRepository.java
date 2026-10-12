package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.PatientVisit;
import com.study.management.domain.model.StudyEnrollment;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.VisitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

interface JpaPatientVisitRepository extends JpaRepository<PatientVisit, UUID> {
    List<PatientVisit> findByEnrollmentOrderByPlanVisit_VisitNumber(StudyEnrollment enrollment);
    List<PatientVisit> findByEnrollment(StudyEnrollment enrollment);
    List<PatientVisit> findByVisitStatusIn(List<VisitStatus> statuses);

    @Query("SELECT pv FROM PatientVisit pv " +
           "JOIN pv.enrollment e " +
           "WHERE e.study = :study " +
           "ORDER BY e.subjectNumber ASC, pv.planVisit.visitNumber ASC")
    List<PatientVisit> findByStudyOrdered(@Param("study") StudyEntity study);

    // Covers both paths: team member (CO_INVESTIGATOR/CRC/CRO) AND primary investigator
    @Query("SELECT DISTINCT pv FROM PatientVisit pv " +
           "JOIN pv.enrollment e " +
           "JOIN e.study s " +
           "JOIN s.studyTeam st " +
           "WHERE (" +
           "  EXISTS (SELECT 1 FROM StudyTeamMember m WHERE m.studyTeam = st " +
           "          AND (m.phone = :phone OR m.email = :phone)) " +
           "  OR st.primaryInvestigator.phone = :phone " +
           ") " +
           "AND (:studyId IS NULL OR s.studyId = :studyId) " +
           "AND (:status IS NULL OR pv.visitStatus = :status) " +
           "AND pv.scheduledDate BETWEEN :from AND :to " +
           "ORDER BY pv.scheduledDate ASC")
    List<PatientVisit> findByTeamMemberPhoneFiltered(
            @Param("phone") String phone,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("studyId") UUID studyId,
            @Param("status") VisitStatus status);
}
