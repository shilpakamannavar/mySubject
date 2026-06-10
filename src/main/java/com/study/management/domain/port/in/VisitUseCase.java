package com.study.management.domain.port.in;

import com.study.management.domain.model.PatientVisit;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface VisitUseCase {
    List<PatientVisit> getVisitSchedule(UUID enrollmentId);
    PatientVisit completeVisit(UUID visitId, PatientVisit updates);
    PatientVisit markMissed(UUID visitId);
    PatientVisit reschedule(UUID visitId, LocalDate newDate);
}
