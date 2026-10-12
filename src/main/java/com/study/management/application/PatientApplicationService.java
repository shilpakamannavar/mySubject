package com.study.management.application;

import com.study.management.domain.model.PatientEntity;
import com.study.management.domain.model.PatientStatus;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.domain.port.in.PatientUseCase;
import com.study.management.domain.port.out.PatientPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Transactional
public class PatientApplicationService implements PatientUseCase {

    private final PatientPort patientPort;

    public PatientApplicationService(PatientPort patientPort) {
        this.patientPort = patientPort;
    }

    @Override
    public PatientEntity createPatient(PatientEntity patient) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = patientPort.countByNumberPrefix("PAT-" + today);
        String patientNumber = String.format("PAT-%s-%03d", today, count + 1);
        patient.setPatientNumber(patientNumber);
        return patientPort.save(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientEntity getById(UUID patientId) {
        return patientPort.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", patientId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientEntity> searchPatients(String search, PatientStatus status, Pageable pageable) {
        return patientPort.search(search, status, pageable);
    }
}
