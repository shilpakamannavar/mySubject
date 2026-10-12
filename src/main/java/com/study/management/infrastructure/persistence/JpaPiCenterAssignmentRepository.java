package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.AssignmentStatus;
import com.study.management.domain.model.MedicalCenter;
import com.study.management.domain.model.PiCenterAssignment;
import com.study.management.domain.model.PrimaryInvestigator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface JpaPiCenterAssignmentRepository extends JpaRepository<PiCenterAssignment, UUID> {
    List<PiCenterAssignment> findByPrimaryInvestigator(PrimaryInvestigator pi);
    List<PiCenterAssignment> findByPrimaryInvestigatorAndStatus(PrimaryInvestigator pi, AssignmentStatus status);
    Optional<PiCenterAssignment> findByPrimaryInvestigatorAndMedicalCenter(PrimaryInvestigator pi, MedicalCenter mc);
    List<PiCenterAssignment> findByMedicalCenter(MedicalCenter center);
}
