package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.PatientEntity;
import com.study.management.domain.model.PatientVisit;
import com.study.management.domain.model.StudyEnrollment;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.VisitStatus;
import com.study.management.domain.port.out.EnrollmentPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class EnrollmentPersistenceAdapter implements EnrollmentPort {

    private final JpaStudyEnrollmentRepository enrollJpa;
    private final JpaPatientVisitRepository visitJpa;

    public EnrollmentPersistenceAdapter(JpaStudyEnrollmentRepository enrollJpa,
                                         JpaPatientVisitRepository visitJpa) {
        this.enrollJpa = enrollJpa;
        this.visitJpa = visitJpa;
    }

    @Override public StudyEnrollment save(StudyEnrollment e)             { return enrollJpa.save(e); }
    @Override public Optional<StudyEnrollment> findById(UUID id)         { return enrollJpa.findById(id); }
    @Override public boolean existsByStudyAndPatient(StudyEntity s, PatientEntity p) {
        return enrollJpa.existsByStudyAndPatient(s, p);
    }
    @Override public Page<StudyEnrollment> findByStudy(StudyEntity s, Pageable p) {
        return enrollJpa.findByStudy(s, p);
    }
    @Override public PatientVisit saveVisit(PatientVisit v)              { return visitJpa.save(v); }
    @Override public List<PatientVisit> findVisitsByEnrollment(StudyEnrollment e) {
        return visitJpa.findByEnrollmentOrderByPlanVisit_VisitNumber(e);
    }
    @Override public List<StudyEnrollment> findByPatient(PatientEntity p) {
        return enrollJpa.findByPatient(p);
    }
    @Override public List<PatientVisit> findVisitsByEnrollmentAndStatusIn(StudyEnrollment e, List<VisitStatus> statuses) {
        return visitJpa.findByEnrollment(e).stream()
                .filter(v -> statuses.contains(v.getVisitStatus()))
                .collect(Collectors.toList());
    }
}
