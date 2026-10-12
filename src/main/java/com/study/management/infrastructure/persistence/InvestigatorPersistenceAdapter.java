package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.AssignmentStatus;
import com.study.management.domain.model.MedicalCenter;
import com.study.management.domain.model.PiCenterAssignment;
import com.study.management.domain.model.PrimaryInvestigator;
import com.study.management.domain.port.out.InvestigatorPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InvestigatorPersistenceAdapter implements InvestigatorPort {

    private final JpaPrimaryInvestigatorRepository piJpa;
    private final JpaPiCenterAssignmentRepository assignJpa;

    public InvestigatorPersistenceAdapter(JpaPrimaryInvestigatorRepository piJpa,
                                           JpaPiCenterAssignmentRepository assignJpa) {
        this.piJpa = piJpa;
        this.assignJpa = assignJpa;
    }

    @Override public PrimaryInvestigator save(PrimaryInvestigator pi)       { return piJpa.save(pi); }
    @Override public Optional<PrimaryInvestigator> findById(UUID piId)      { return piJpa.findById(piId); }
    @Override public List<PrimaryInvestigator> findAll()                    { return piJpa.findAll(); }
    @Override public Optional<PrimaryInvestigator> findByPhone(String p)        { return piJpa.findByPhone(p); }
    @Override public Optional<PrimaryInvestigator> findByEmail(String email)   { return piJpa.findByEmailIgnoreCase(email); }
    @Override public boolean existsByEmail(String email)                        { return piJpa.existsByEmail(email); }
    @Override public PiCenterAssignment saveAssignment(PiCenterAssignment a) { return assignJpa.save(a); }
    @Override public List<PiCenterAssignment> findAssignmentsByPi(PrimaryInvestigator pi) {
        return assignJpa.findByPrimaryInvestigator(pi);
    }
    @Override public List<PiCenterAssignment> findAssignmentsByPiAndStatus(PrimaryInvestigator pi, AssignmentStatus s) {
        return assignJpa.findByPrimaryInvestigatorAndStatus(pi, s);
    }
    @Override public List<PiCenterAssignment> findAssignmentsByCenter(MedicalCenter center) {
        return assignJpa.findByMedicalCenter(center);
    }
}
