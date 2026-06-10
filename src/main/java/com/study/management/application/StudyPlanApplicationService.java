package com.study.management.application;

import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.StudyPlanVisit;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.domain.port.in.StudyPlanUseCase;
import com.study.management.domain.port.out.ClinicalStudyPort;
import com.study.management.domain.port.out.StudyPlanPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class StudyPlanApplicationService implements StudyPlanUseCase {

    private final StudyPlanPort planPort;
    private final ClinicalStudyPort studyPort;

    public StudyPlanApplicationService(StudyPlanPort planPort, ClinicalStudyPort studyPort) {
        this.planPort = planPort;
        this.studyPort = studyPort;
    }

    @Override
    public List<StudyPlanVisit> upsertPlanVisits(UUID studyId, List<StudyPlanVisit> visits) {
        StudyEntity study = studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));

        // Delete existing visits
        planPort.deleteByStudy(study);

        // Save new visits
        List<StudyPlanVisit> saved = new ArrayList<>();
        for (StudyPlanVisit visit : visits) {
            visit.setStudy(study);
            saved.add(planPort.save(visit));
        }

        // Recalculate rate and total cost
        BigDecimal totalRate = saved.stream()
                .map(v -> v.getVisitCost() != null ? v.getVisitCost() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        study.setCalculatedRatePerService(totalRate);

        Integer patients = study.getNoOfPatientsToEnroll();
        if (patients != null && patients > 0) {
            study.setTotalCost(totalRate.multiply(BigDecimal.valueOf(patients)));
        }
        studyPort.save(study);

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyPlanVisit> getPlanVisits(UUID studyId) {
        StudyEntity study = studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));
        return planPort.findByStudyOrdered(study);
    }
}
