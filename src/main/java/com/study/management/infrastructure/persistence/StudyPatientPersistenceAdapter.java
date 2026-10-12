package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.Study;
import com.study.management.domain.model.StudyPatient;
import com.study.management.domain.port.out.StudyPatientPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StudyPatientPersistenceAdapter implements StudyPatientPort {

    private final JpaStudyPatientRepository jpa;

    public StudyPatientPersistenceAdapter(JpaStudyPatientRepository jpa) {
        this.jpa = jpa;
    }

    @Override public StudyPatient              save(StudyPatient p)                            { return jpa.save(p); }
    @Override public List<StudyPatient>        findByStudy(Study s)                           { return jpa.findByStudyOrderByPatientNumber(s); }
    @Override public Optional<StudyPatient>    findByStudyAndPatientNumber(Study s, String n) { return jpa.findByStudyAndPatientNumber(s, n); }
    @Override public int                       countByStudy(Study s)                          { return jpa.countByStudy(s); }
}
