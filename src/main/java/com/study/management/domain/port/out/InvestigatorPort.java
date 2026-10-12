package com.study.management.domain.port.out;

import com.study.management.domain.model.AssignmentStatus;
import com.study.management.domain.model.MedicalCenter;
import com.study.management.domain.model.PiCenterAssignment;
import com.study.management.domain.model.PrimaryInvestigator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvestigatorPort {
    PrimaryInvestigator save(PrimaryInvestigator pi);
    Optional<PrimaryInvestigator> findById(UUID piId);
    List<PrimaryInvestigator> findAll();
    Optional<PrimaryInvestigator> findByPhone(String phone);
    Optional<PrimaryInvestigator> findByEmail(String email);
    boolean existsByEmail(String email);
    PiCenterAssignment saveAssignment(PiCenterAssignment assignment);
    List<PiCenterAssignment> findAssignmentsByPi(PrimaryInvestigator pi);
    List<PiCenterAssignment> findAssignmentsByPiAndStatus(PrimaryInvestigator pi, AssignmentStatus status);
    List<PiCenterAssignment> findAssignmentsByCenter(MedicalCenter center);
}
