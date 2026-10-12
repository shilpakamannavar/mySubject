package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.PatientStudyLog;
import com.study.management.domain.model.StudyEnrollment;
import com.study.management.domain.model.StudyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaPatientStudyLogRepository extends JpaRepository<PatientStudyLog, String> {

    List<PatientStudyLog> findByEnrollmentOrderByVisitNumber(StudyEnrollment enrollment);

    List<PatientStudyLog> findByStudyOrderByVisitNumber(StudyEntity study);

    @Modifying
    @Query("UPDATE PatientStudyLog l SET l.isReadOnly = true WHERE l.study = :study")
    void lockReadOnlyByStudy(@Param("study") StudyEntity study);
}
