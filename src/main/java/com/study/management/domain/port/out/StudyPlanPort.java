package com.study.management.domain.port.out;

import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.StudyPlanVisit;

import java.util.List;

public interface StudyPlanPort {
    void deleteByStudy(StudyEntity study);
    void flush();
    StudyPlanVisit save(StudyPlanVisit visit);
    List<StudyPlanVisit> findByStudyOrdered(StudyEntity study);
}
