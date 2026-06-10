package com.study.management.application;

import com.study.management.domain.model.PatientVisit;
import com.study.management.domain.model.PrimaryInvestigator;
import com.study.management.domain.model.StudyEnrollment;
import com.study.management.domain.model.VisitStatus;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.domain.port.in.VisitUseCase;
import com.study.management.domain.port.out.EnrollmentPort;
import com.study.management.domain.port.out.InvestigatorPort;
import com.study.management.domain.port.out.VisitPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class VisitApplicationService implements VisitUseCase {

    private final VisitPort visitPort;
    private final EnrollmentPort enrollmentPort;
    private final InvestigatorPort investigatorPort;

    public VisitApplicationService(VisitPort visitPort,
                                    EnrollmentPort enrollmentPort,
                                    InvestigatorPort investigatorPort) {
        this.visitPort = visitPort;
        this.enrollmentPort = enrollmentPort;
        this.investigatorPort = investigatorPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientVisit> getVisitSchedule(UUID enrollmentId) {
        StudyEnrollment enrollment = enrollmentPort.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));
        return visitPort.findByEnrollmentOrdered(enrollment);
    }

    @Override
    public PatientVisit completeVisit(UUID visitId, PatientVisit updates) {
        PatientVisit visit = visitPort.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit", visitId));
        visit.setVisitStatus(VisitStatus.COMPLETED);
        if (updates.getActualDate() != null)          visit.setActualDate(updates.getActualDate());
        if (updates.getCheckInTime() != null)         visit.setCheckInTime(updates.getCheckInTime());
        if (updates.getCheckOutTime() != null)        visit.setCheckOutTime(updates.getCheckOutTime());
        if (updates.getDurationMinutes() != null)     visit.setDurationMinutes(updates.getDurationMinutes());
        if (updates.getVitalsRecorded() != null)      visit.setVitalsRecorded(updates.getVitalsRecorded());
        if (updates.getAdverseEvents() != null)       visit.setAdverseEvents(updates.getAdverseEvents());
        if (updates.getLabSamplesTaken() != null)     visit.setLabSamplesTaken(updates.getLabSamplesTaken());
        if (updates.getProceduresDone() != null)      visit.setProceduresDone(updates.getProceduresDone());
        if (updates.getInvestigatorNotes() != null)   visit.setInvestigatorNotes(updates.getInvestigatorNotes());
        if (updates.getCoordinatorNotes() != null)    visit.setCoordinatorNotes(updates.getCoordinatorNotes());
        if (updates.getPatientReported() != null)     visit.setPatientReported(updates.getPatientReported());
        if (updates.getPerformedByPi() != null && updates.getPerformedByPi().getPiId() != null) {
            PrimaryInvestigator pi = investigatorPort.findById(updates.getPerformedByPi().getPiId())
                    .orElseThrow(() -> new ResourceNotFoundException("PrimaryInvestigator",
                            updates.getPerformedByPi().getPiId()));
            visit.setPerformedByPi(pi);
        }
        return visitPort.save(visit);
    }

    @Override
    public PatientVisit markMissed(UUID visitId) {
        PatientVisit visit = visitPort.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit", visitId));
        visit.setVisitStatus(VisitStatus.MISSED);
        return visitPort.save(visit);
    }

    @Override
    public PatientVisit reschedule(UUID visitId, LocalDate newDate) {
        PatientVisit visit = visitPort.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit", visitId));
        visit.setScheduledDate(newDate);
        visit.setVisitStatus(VisitStatus.RESCHEDULED);
        return visitPort.save(visit);
    }
}
