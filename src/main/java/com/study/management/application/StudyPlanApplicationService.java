package com.study.management.application;

import com.study.management.domain.model.ClinicalStudyStatus;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.StudyPlanVisit;
import com.study.management.domain.model.StudyTeam;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.domain.port.in.StudyPlanUseCase;
import com.study.management.domain.port.out.ClinicalStudyPort;
import com.study.management.domain.port.out.StudyPlanPort;
import com.study.management.domain.port.out.StudyTeamPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class StudyPlanApplicationService implements StudyPlanUseCase {

    private final StudyPlanPort planPort;
    private final ClinicalStudyPort studyPort;
    private final StudyTeamPort studyTeamPort;

    public StudyPlanApplicationService(StudyPlanPort planPort, ClinicalStudyPort studyPort,
                                       StudyTeamPort studyTeamPort) {
        this.planPort = planPort;
        this.studyPort = studyPort;
        this.studyTeamPort = studyTeamPort;
    }

    @Override
    public List<StudyPlanVisit> upsertPlanVisits(UUID studyId, List<StudyPlanVisit> visits) {
        if (visits.size() > 20) {
            throw new IllegalArgumentException("Study plan cannot exceed 20 visits.");
        }
        StudyEntity study = studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));

        // Delete existing visits and flush so inserts don't collide with the unique constraint
        planPort.deleteByStudy(study);
        planPort.flush();

        // Save new visits
        List<StudyPlanVisit> saved = new ArrayList<>();
        for (int i = 0; i < visits.size(); i++) {
            StudyPlanVisit visit = visits.get(i);
            visit.setStudy(study);
            if (visit.getSortOrder() == null) visit.setSortOrder(i + 1);
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

    @Override
    public StudyEntity createStudyTemplate(StudyEntity study, List<StudyPlanVisit> visits) {
        if (visits != null && visits.size() > 20) {
            throw new IllegalArgumentException("Study plan cannot exceed 20 visits.");
        }
        if (study.getStudyNumber() == null || study.getStudyNumber().isBlank()) {
            String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String suffix   = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
            study.setStudyNumber("STU-" + datePart + "-" + suffix);
        } else if (studyPort.existsByStudyNumber(study.getStudyNumber())) {
            throw new IllegalArgumentException("Study number '" + study.getStudyNumber() + "' already exists.");
        }
        if (study.getStudyTeam() == null || study.getStudyTeam().getTrialId() == null) {
            throw new IllegalArgumentException("studyTeamId is required.");
        }
        StudyTeam team = studyTeamPort.findById(study.getStudyTeam().getTrialId())
                .orElseThrow(() -> new ResourceNotFoundException("StudyTeam", study.getStudyTeam().getTrialId()));
        study.setStudyTeam(team);
        study.setCreatedByPi(team.getPrimaryInvestigator());
        study.setStatus(ClinicalStudyStatus.INACTIVE);
        if (study.getStudyName() == null || study.getStudyName().isBlank()) {
            study.setStudyName(study.getStudyNumber());
        }
        StudyEntity saved = studyPort.save(study);

        if (visits != null) {
            for (int i = 0; i < visits.size(); i++) {
                StudyPlanVisit v = visits.get(i);
                v.setStudy(saved);
                if (v.getSortOrder() == null) v.setSortOrder(i + 1);
                planPort.save(v);
            }
        }
        return saved;
    }

    @Override
    public StudyEntity updateStudyTemplate(UUID studyId, StudyEntity updates, List<StudyPlanVisit> visits) {
        if (visits != null && visits.size() > 20) {
            throw new IllegalArgumentException("Study plan cannot exceed 20 visits.");
        }
        StudyEntity study = studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));
        if (updates.getIndication()              != null) study.setIndication(updates.getIndication());
        if (updates.getNoOfPatientsToEnroll()    != null) study.setNoOfPatientsToEnroll(updates.getNoOfPatientsToEnroll());
        if (updates.getTotalNumberOfVisits()     != null) study.setTotalNumberOfVisits(updates.getTotalNumberOfVisits());
        studyPort.save(study);

        if (visits != null) {
            planPort.deleteByStudy(study);
            planPort.flush();
            for (int i = 0; i < visits.size(); i++) {
                StudyPlanVisit v = visits.get(i);
                v.setStudy(study);
                if (v.getSortOrder() == null) v.setSortOrder(i + 1);
                planPort.save(v);
            }
        }
        return study;
    }
}
