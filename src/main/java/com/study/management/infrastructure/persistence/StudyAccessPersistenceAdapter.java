package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.StudyAccess;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.UserRole;
import com.study.management.domain.port.out.StudyAccessPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class StudyAccessPersistenceAdapter implements StudyAccessPort {

    private final JpaStudyAccessRepository jpa;

    public StudyAccessPersistenceAdapter(JpaStudyAccessRepository jpa) {
        this.jpa = jpa;
    }

    @Override public StudyAccess save(StudyAccess a)                         { return jpa.save(a); }
    @Override public Optional<StudyAccess> findById(UUID id)                 { return jpa.findById(id); }
    @Override public List<StudyAccess> findByStudy(StudyEntity s)            { return jpa.findByStudy(s); }
    @Override public List<StudyAccess> findByStudyAndActive(StudyEntity s, boolean active) {
        return jpa.findByStudyAndIsActive(s, active);
    }
    @Override public List<StudyAccess> findByStudyAndActiveExcludingRole(StudyEntity s, boolean active, UserRole role) {
        return jpa.findByStudyAndIsActiveAndUserRoleNot(s, active, role);
    }
}
