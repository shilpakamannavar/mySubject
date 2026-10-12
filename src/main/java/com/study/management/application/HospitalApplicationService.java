package com.study.management.application;

import com.study.management.domain.model.Hospital;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.domain.port.in.HospitalUseCase;
import com.study.management.domain.port.out.HospitalPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class HospitalApplicationService implements HospitalUseCase {

    private final HospitalPort hospitalPort;

    public HospitalApplicationService(HospitalPort hospitalPort) {
        this.hospitalPort = hospitalPort;
    }

    @Override
    public Hospital create(Hospital hospital) {
        if (hospital.getEmail() != null && hospitalPort.existsByEmail(hospital.getEmail())) {
            throw new IllegalArgumentException("Hospital with email '" + hospital.getEmail() + "' already exists.");
        }
        if (hospital.getPhone() != null && hospitalPort.existsByPhone(hospital.getPhone())) {
            throw new IllegalArgumentException("Hospital with phone '" + hospital.getPhone() + "' already exists.");
        }
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String suffix   = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        hospital.setHospitalNumber("HOSP-" + datePart + "-" + suffix);
        return hospitalPort.save(hospital);
    }

    @Override
    @Transactional(readOnly = true)
    public Hospital getById(UUID id) {
        return hospitalPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hospital> listAll() {
        return hospitalPort.findAll();
    }
}