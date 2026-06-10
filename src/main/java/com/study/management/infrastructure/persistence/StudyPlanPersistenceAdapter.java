package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.StudyPlanVisit;
import com.study.management.domain.port.out.StudyPlanPort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StudyPlanPersistenceAdapter implements StudyPlanPort {

    private final JpaStudyPlanVisitRepository jpa;

    public StudyPlanPersistenceAdapter(JpaStudyPlanVisitRepository jpa) {
        this.jpa = jpa;
    }

    @Override public void deleteByStudy(StudyEntity study)          { jpa.deleteByStudy(study); }
    @Override public StudyPlanVisit save(StudyPlanVisit v)          { return jpa.save(v); }
    @Override public List<StudyPlanVisit> findByStudyOrdered(StudyEntity study) {
        return jpa.findByStudyOrderByVisitNumber(study);
    }
}
