package com.study.management.domain.port.out;

import com.study.management.domain.model.StudyAccess;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudyAccessPort {
    StudyAccess save(StudyAccess access);
    Optional<StudyAccess> findById(UUID accessId);
    List<StudyAccess> findByStudy(StudyEntity study);
    List<StudyAccess> findByStudyAndActive(StudyEntity study, boolean active);
    List<StudyAccess> findByStudyAndActiveExcludingRole(StudyEntity study, boolean active, UserRole role);
}
