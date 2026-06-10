package com.study.management.domain.port.in;

import com.study.management.domain.model.StudyPatient;

import java.util.List;

public interface StudyPatientUseCase {

    StudyPatient addPatient(String studyId, StudyPatient patient);

    List<StudyPatient> getPatientsByStudy(String studyId);

    StudyPatient activatePatient(String studyId, String patientNumber);
}
