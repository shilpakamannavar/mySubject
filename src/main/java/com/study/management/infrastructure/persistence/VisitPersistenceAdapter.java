package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.PatientVisit;
import com.study.management.domain.model.StudyEnrollment;
import com.study.management.domain.port.out.VisitPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class VisitPersistenceAdapter implements VisitPort {

    private final JpaPatientVisitRepository jpa;

    public VisitPersistenceAdapter(JpaPatientVisitRepository jpa) {
        this.jpa = jpa;
    }

    @Override public Optional<PatientVisit> findById(UUID id)      { return jpa.findById(id); }
    @Override public PatientVisit save(PatientVisit v)             { return jpa.save(v); }
    @Override public List<PatientVisit> findByEnrollmentOrdered(StudyEnrollment e) {
        return jpa.findByEnrollmentOrderByPlanVisit_VisitNumber(e);
    }
}
