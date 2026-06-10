package com.study.management.domain.port.in;

import com.study.management.domain.model.StudyPlanVisit;

import java.util.List;
import java.util.UUID;

public interface StudyPlanUseCase {
    List<StudyPlanVisit> upsertPlanVisits(UUID studyId, List<StudyPlanVisit> visits);
    List<StudyPlanVisit> getPlanVisits(UUID studyId);
}
