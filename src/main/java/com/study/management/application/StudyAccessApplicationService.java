package com.study.management.application;

import com.study.management.domain.model.StudyAccess;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.domain.port.in.StudyAccessUseCase;
import com.study.management.domain.port.out.ClinicalStudyPort;
import com.study.management.domain.port.out.StudyAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class StudyAccessApplicationService implements StudyAccessUseCase {

    private final StudyAccessPort accessPort;
    private final ClinicalStudyPort studyPort;

    public StudyAccessApplicationService(StudyAccessPort accessPort, ClinicalStudyPort studyPort) {
        this.accessPort = accessPort;
        this.studyPort = studyPort;
    }

    @Override
    public StudyAccess grantAccess(UUID studyId, StudyAccess access) {
        StudyEntity study = studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));
        access.setStudy(study);
        access.setIsActive(true);
        access.setGrantedAt(OffsetDateTime.now());
        return accessPort.save(access);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyAccess> listAccess(UUID studyId, Boolean active) {
        StudyEntity study = studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));
        if (active != null) {
            return accessPort.findByStudyAndActive(study, active);
        }
        return accessPort.findByStudy(study);
    }

    @Override
    public StudyAccess revokeAccess(UUID studyId, UUID accessId) {
        studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));
        StudyAccess access = accessPort.findById(accessId)
                .orElseThrow(() -> new ResourceNotFoundException("StudyAccess", accessId));
        access.setIsActive(false);
        access.setRevokedAt(OffsetDateTime.now());
        return accessPort.save(access);
    }
}
