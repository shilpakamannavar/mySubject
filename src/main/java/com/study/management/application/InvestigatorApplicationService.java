package com.study.management.application;

import com.study.management.domain.model.AssignmentStatus;
import com.study.management.domain.model.MedicalCenter;
import com.study.management.domain.model.PiCenterAssignment;
import com.study.management.domain.model.PrimaryInvestigator;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.domain.port.in.InvestigatorUseCase;
import com.study.management.domain.port.out.InvestigatorPort;
import com.study.management.domain.port.out.MedicalCenterPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class InvestigatorApplicationService implements InvestigatorUseCase {

    private final InvestigatorPort investigatorPort;
    private final MedicalCenterPort centerPort;

    public InvestigatorApplicationService(InvestigatorPort investigatorPort,
                                           MedicalCenterPort centerPort) {
        this.investigatorPort = investigatorPort;
        this.centerPort = centerPort;
    }

    @Override
    public PrimaryInvestigator createInvestigator(PrimaryInvestigator pi) {
        // case-insensitive email check (findByEmail delegates to findByEmailIgnoreCase)
        if (pi.getEmail() != null && investigatorPort.findByEmail(pi.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Investigator with email '" + pi.getEmail() + "' already exists.");
        }
        // phone check covers users who registered in Cognito with phone only (no email)
        if (pi.getPhone() != null && investigatorPort.findByPhone(pi.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Investigator with phone '" + pi.getPhone() + "' already exists.");
        }
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String suffix   = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        pi.setPiNumber("PI-" + datePart + "-" + suffix);
        return investigatorPort.save(pi);
    }

    @Override
    public PiCenterAssignment assignToCenter(UUID piId, PiCenterAssignment assignment) {
        PrimaryInvestigator pi = investigatorPort.findById(piId)
                .orElseThrow(() -> new ResourceNotFoundException("PrimaryInvestigator", piId));
        MedicalCenter center = centerPort.findById(assignment.getMedicalCenter().getCenterId())
                .orElseThrow(() -> new ResourceNotFoundException("MedicalCenter",
                        assignment.getMedicalCenter().getCenterId()));
        assignment.setPrimaryInvestigator(pi);
        assignment.setMedicalCenter(center);
        return investigatorPort.saveAssignment(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public PrimaryInvestigator getById(UUID piId) {
        return investigatorPort.findById(piId)
                .orElseThrow(() -> new ResourceNotFoundException("PrimaryInvestigator", piId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrimaryInvestigator> listAll() {
        return investigatorPort.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PiCenterAssignment> getAssignments(UUID piId, AssignmentStatus status) {
        PrimaryInvestigator pi = getById(piId);
        if (status != null) {
            return investigatorPort.findAssignmentsByPiAndStatus(pi, status);
        }
        return investigatorPort.findAssignmentsByPi(pi);
    }
}
