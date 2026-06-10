package com.study.management.domain.port.in;

import com.study.management.domain.model.ClinicalStudyStatus;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.StudySiteAssignment;

import java.util.List;
import java.util.UUID;

public interface ClinicalStudyUseCase {
    StudyEntity createStudy(StudyEntity study);
    StudyEntity getById(UUID studyId);
    List<StudyEntity> getStudiesForPi(UUID piId, ClinicalStudyStatus status);
    StudyEntity updateStatus(UUID studyId, ClinicalStudyStatus status, String reason);
    StudySiteAssignment assignSite(UUID studyId, StudySiteAssignment assignment);
}
