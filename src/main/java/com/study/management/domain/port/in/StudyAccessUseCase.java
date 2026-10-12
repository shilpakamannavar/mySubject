package com.study.management.domain.port.in;

import com.study.management.domain.model.StudyAccess;

import java.util.List;
import java.util.UUID;

public interface StudyAccessUseCase {
    StudyAccess grantAccess(UUID studyId, StudyAccess access);
    List<StudyAccess> listAccess(UUID studyId, Boolean active);
    StudyAccess revokeAccess(UUID studyId, UUID accessId);
}
