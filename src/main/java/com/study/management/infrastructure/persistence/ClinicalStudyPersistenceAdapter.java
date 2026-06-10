package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.ClinicalStudyStatus;
import com.study.management.domain.model.PrimaryInvestigator;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.StudySiteAssignment;
import com.study.management.domain.port.out.ClinicalStudyPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ClinicalStudyPersistenceAdapter implements ClinicalStudyPort {

    private final JpaClinicalStudyRepository studyJpa;
    private final JpaStudySiteAssignmentRepository siteJpa;

    public ClinicalStudyPersistenceAdapter(JpaClinicalStudyRepository studyJpa,
                                            JpaStudySiteAssignmentRepository siteJpa) {
        this.studyJpa = studyJpa;
        this.siteJpa = siteJpa;
    }

    @Override public StudyEntity save(StudyEntity s)                          { return studyJpa.save(s); }
    @Override public Optional<StudyEntity> findById(UUID id)                  { return studyJpa.findById(id); }
    @Override public boolean existsByStudyNumber(String n)                    { return studyJpa.existsByStudyNumber(n); }
    @Override public List<StudyEntity> findByPi(PrimaryInvestigator pi)       { return studyJpa.findByCreatedByPi(pi); }
    @Override public List<StudyEntity> findByPiAndStatus(PrimaryInvestigator pi, ClinicalStudyStatus s) {
        return studyJpa.findByCreatedByPiAndStatus(pi, s, org.springframework.data.domain.Pageable.unpaged()).getContent();
    }
    @Override public StudySiteAssignment saveSiteAssignment(StudySiteAssignment a) { return siteJpa.save(a); }
    @Override public List<StudySiteAssignment> findSitesByStudy(StudyEntity s) { return siteJpa.findByStudy(s); }
}
