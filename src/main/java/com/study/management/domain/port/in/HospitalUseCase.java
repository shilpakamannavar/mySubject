package com.study.management.domain.port.in;

import com.study.management.domain.model.Hospital;

import java.util.List;
import java.util.UUID;

public interface HospitalUseCase {
    Hospital create(Hospital hospital);
    Hospital getById(UUID id);
    List<Hospital> listAll();
}