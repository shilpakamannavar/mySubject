package com.study.management.application;

import com.study.management.domain.model.Study;
import com.study.management.domain.model.StudyPatient;
import com.study.management.domain.model.StudyStatus;
import com.study.management.domain.port.in.StudyPatientUseCase;
import com.study.management.domain.port.out.StudyPatientPort;
import com.study.management.domain.port.out.StudyPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudyPatientApplicationService implements StudyPatientUseCase {

    private final StudyPatientPort patientPort;
    private final StudyPort studyPort;

    public StudyPatientApplicationService(StudyPatientPort patientPort, StudyPort studyPort) {
        this.patientPort = patientPort;
        this.studyPort   = studyPort;
    }

    @Override
    public StudyPatient addPatient(String studyId, StudyPatient patient) {
        Study study = studyPort.findByStudyId(studyId)
                .orElseThrow(() -> new IllegalArgumentException("Study not found: " + studyId));

        if (study.getStatus() != StudyStatus.ACTIVE) {
            throw new IllegalStateException("Study must be ACTIVE to add patients. Current status: " + study.getStatus());
        }

        int count = patientPort.countByStudy(study);
        String patientNumber = String.valueOf(11100 + count + 1);

        patient.setPatientNumber(patientNumber);
        patient.setPatientInitial(patient.getPatientInitial().toUpperCase());
        patient.setStudy(study);
        patient.setStatus(StudyPatient.PatientStatus.INACTIVE);

        return patientPort.save(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyPatient> getPatientsByStudy(String studyId) {
        Study study = studyPort.findByStudyId(studyId)
                .orElseThrow(() -> new IllegalArgumentException("Study not found: " + studyId));
        return patientPort.findByStudy(study);
    }

    @Override
    public StudyPatient activatePatient(String studyId, String patientNumber) {
        Study study = studyPort.findByStudyId(studyId)
                .orElseThrow(() -> new IllegalArgumentException("Study not found: " + studyId));

        StudyPatient patient = patientPort.findByStudyAndPatientNumber(study, patientNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Patient " + patientNumber + " not found in study " + studyId));

        patient.setStatus(StudyPatient.PatientStatus.ACTIVE);
        return patientPort.save(patient);
    }
}
