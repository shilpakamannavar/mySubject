package com.study.management.domain.port.out;

import com.study.management.domain.model.PatientEntity;
import com.study.management.domain.model.PatientStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface PatientPort {
    PatientEntity save(PatientEntity patient);
    Optional<PatientEntity> findById(UUID patientId);
    long countByNumberPrefix(String prefix);
    Page<PatientEntity> search(String search, PatientStatus status, Pageable pageable);
}
