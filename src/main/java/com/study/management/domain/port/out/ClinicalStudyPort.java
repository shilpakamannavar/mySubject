package com.study.management.domain.port.out;

import com.study.management.domain.model.ClinicalStudyStatus;
import com.study.management.domain.model.PrimaryInvestigator;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.StudySiteAssignment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClinicalStudyPort {
    StudyEntity save(StudyEntity study);
    Optional<StudyEntity> findById(UUID studyId);
    boolean existsByStudyNumber(String studyNumber);
    List<StudyEntity> findByPi(PrimaryInvestigator pi);
    List<StudyEntity> findByPiAndStatus(PrimaryInvestigator pi, ClinicalStudyStatus status);
    StudySiteAssignment saveSiteAssignment(StudySiteAssignment assignment);
    List<StudySiteAssignment> findSitesByStudy(StudyEntity study);
}
