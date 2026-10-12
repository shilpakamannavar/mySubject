package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.PatientVisit;
import com.study.management.domain.model.StudyEnrollment;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.VisitStatus;
import com.study.management.domain.port.out.VisitPort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
    @Override public List<PatientVisit> findByStudyOrdered(StudyEntity study) {
        return jpa.findByStudyOrdered(study);
    }
    @Override public List<PatientVisit> findByTeamMemberPhone(String phone, LocalDate from, LocalDate to,
                                                               UUID studyId, VisitStatus status) {
        return jpa.findByTeamMemberPhoneFiltered(phone, from, to, studyId, status);
    }
}
