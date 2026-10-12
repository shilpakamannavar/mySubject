package com.study.management.domain.port.in;

import com.study.management.domain.model.ClinicalStudyStatus;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.StudySiteAssignment;

import java.util.List;
import java.util.UUID;

public interface ClinicalStudyUseCase {
    StudyEntity createStudy(StudyEntity study, String callerUserId, String callerRole);
    StudyEntity getById(UUID studyId);
    List<StudyEntity> getAllStudies();
    List<StudyEntity> getStudiesForPi(UUID piId, ClinicalStudyStatus status);
    List<StudyEntity> getStudiesForPiByPhone(String phone);
    List<StudyEntity> getDesignStudies(String callerPhone, String callerRole);
    StudyEntity updateStudyDesign(UUID studyId, StudyEntity updates, String callerPhone, String callerRole);
    StudyEntity updateStatus(UUID studyId, ClinicalStudyStatus status, String reason);
    StudySiteAssignment assignSite(UUID studyId, StudySiteAssignment assignment);
}
