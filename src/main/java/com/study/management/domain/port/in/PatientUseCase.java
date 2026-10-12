package com.study.management.domain.port.in;

import com.study.management.domain.model.PatientEntity;
import com.study.management.domain.model.PatientStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PatientUseCase {
    PatientEntity createPatient(PatientEntity patient);
    PatientEntity getById(UUID patientId);
    Page<PatientEntity> searchPatients(String search, PatientStatus status, Pageable pageable);
}
