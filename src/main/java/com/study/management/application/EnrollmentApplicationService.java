package com.study.management.application;

import com.study.management.domain.model.ClinicalStudyStatus;
import com.study.management.domain.model.EnrollmentStatus;
import com.study.management.domain.model.PatientEntity;
import com.study.management.domain.model.PatientVisit;
import com.study.management.domain.model.StudyEnrollment;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.StudyPlanVisit;
import com.study.management.domain.model.VisitStatus;
import com.study.management.domain.model.exception.BusinessRuleException;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.domain.port.in.EnrollmentUseCase;
import com.study.management.domain.port.out.ClinicalStudyPort;
import com.study.management.domain.port.out.EnrollmentPort;
import com.study.management.domain.port.out.PatientPort;
import com.study.management.domain.port.out.StudyPlanPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EnrollmentApplicationService implements EnrollmentUseCase {

    private final EnrollmentPort enrollmentPort;
    private final ClinicalStudyPort studyPort;
    private final PatientPort patientPort;
    private final StudyPlanPort planPort;

    public EnrollmentApplicationService(EnrollmentPort enrollmentPort,
                                         ClinicalStudyPort studyPort,
                                         PatientPort patientPort,
                                         StudyPlanPort planPort) {
        this.enrollmentPort = enrollmentPort;
        this.studyPort = studyPort;
        this.patientPort = patientPort;
        this.planPort = planPort;
    }

    @Override
    public StudyEnrollment enrollPatient(UUID studyId, StudyEnrollment enrollment) {
        StudyEntity study = studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));

        if (study.getStatus() != ClinicalStudyStatus.ACTIVE) {
            throw new BusinessRuleException("Cannot enroll patient in a study with status: " + study.getStatus());
        }

        PatientEntity patient = patientPort.findById(enrollment.getPatient().getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient",
                        enrollment.getPatient().getPatientId()));

        if (enrollmentPort.existsByStudyAndPatient(study, patient)) {
            throw new BusinessRuleException("Patient is already enrolled in this study.");
        }

        enrollment.setStudy(study);
        enrollment.setPatient(patient);
        enrollment.setEnrollmentStatus(EnrollmentStatus.ENROLLED);
        StudyEnrollment saved = enrollmentPort.save(enrollment);

        // Auto-generate patient visits from study plan
        List<StudyPlanVisit> planVisits = planPort.findByStudyOrdered(study);
        LocalDate baseDate = saved.getEnrollmentDate() != null ? saved.getEnrollmentDate() : LocalDate.now();
        LocalDate today = LocalDate.now();

        for (StudyPlanVisit planVisit : planVisits) {
            PatientVisit pv = new PatientVisit();
            pv.setEnrollment(saved);
            pv.setPlanVisit(planVisit);

            Integer offset = planVisit.getTargetDayOffset();
            LocalDate scheduledDate = offset != null ? baseDate.plusDays(offset) : baseDate;
            pv.setScheduledDate(scheduledDate);

            Integer before = planVisit.getWindowPeriodBefore();
            Integer after = planVisit.getWindowPeriodAfter();
            pv.setWindowOpenDate(before != null ? scheduledDate.minusDays(before) : scheduledDate);
            pv.setWindowCloseDate(after != null ? scheduledDate.plusDays(after) : scheduledDate);

            // Determine visit status
            boolean isUpcoming = !scheduledDate.isAfter(today.plusDays(7)) && !scheduledDate.isBefore(today);
            pv.setVisitStatus(isUpcoming ? VisitStatus.UPCOMING : VisitStatus.SCHEDULED);

            enrollmentPort.saveVisit(pv);
        }

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudyEnrollment> listEnrollments(UUID studyId, Pageable pageable) {
        StudyEntity study = studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));
        return enrollmentPort.findByStudy(study, pageable);
    }

    @Override
    public StudyEnrollment withdrawPatient(UUID studyId, UUID enrollmentId, StudyEnrollment updates) {
        studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));
        StudyEnrollment enrollment = enrollmentPort.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));

        enrollment.setEnrollmentStatus(EnrollmentStatus.WITHDRAWN);
        if (updates.getWithdrawalDate() != null) enrollment.setWithdrawalDate(updates.getWithdrawalDate());
        if (updates.getWithdrawalReason() != null) enrollment.setWithdrawalReason(updates.getWithdrawalReason());

        // Cancel remaining SCHEDULED/UPCOMING visits
        List<VisitStatus> cancelStatuses = Arrays.asList(VisitStatus.SCHEDULED, VisitStatus.UPCOMING);
        List<PatientVisit> visits = enrollmentPort.findVisitsByEnrollmentAndStatusIn(enrollment, cancelStatuses);
        for (PatientVisit visit : visits) {
            visit.setVisitStatus(VisitStatus.CANCELLED);
            enrollmentPort.saveVisit(visit);
        }

        return enrollmentPort.save(enrollment);
    }
}
