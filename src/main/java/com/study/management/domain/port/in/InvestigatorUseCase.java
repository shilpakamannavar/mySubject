package com.study.management.domain.port.in;

import com.study.management.domain.model.AssignmentStatus;
import com.study.management.domain.model.PiCenterAssignment;
import com.study.management.domain.model.PrimaryInvestigator;

import java.util.List;
import java.util.UUID;

public interface InvestigatorUseCase {
    PrimaryInvestigator createInvestigator(PrimaryInvestigator pi);
    PiCenterAssignment assignToCenter(UUID piId, PiCenterAssignment assignment);
    PrimaryInvestigator getById(UUID piId);
    List<PrimaryInvestigator> listAll();
    List<PiCenterAssignment> getAssignments(UUID piId, AssignmentStatus status);
}
