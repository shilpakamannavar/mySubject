package com.study.management.domain.port.out;

import com.study.management.domain.model.Hospital;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HospitalPort {
    Hospital save(Hospital hospital);
    Optional<Hospital> findById(UUID id);
    List<Hospital> findAll();
    Optional<Hospital> findByPhone(String phone);
    Optional<Hospital> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
