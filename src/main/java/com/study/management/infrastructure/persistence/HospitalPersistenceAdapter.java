package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.Hospital;
import com.study.management.domain.port.out.HospitalPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class HospitalPersistenceAdapter implements HospitalPort {

    private final JpaHospitalRepository jpa;

    public HospitalPersistenceAdapter(JpaHospitalRepository jpa) {
        this.jpa = jpa;
    }

    @Override public Hospital save(Hospital hospital)               { return jpa.save(hospital); }
    @Override public Optional<Hospital> findById(UUID id)           { return jpa.findById(id); }
    @Override public List<Hospital> findAll()                       { return jpa.findAll(); }
    @Override public Optional<Hospital> findByPhone(String phone)   { return jpa.findByPhone(phone); }
    @Override public Optional<Hospital> findByEmail(String email)   { return jpa.findByEmailIgnoreCase(email); }
    @Override public boolean existsByEmail(String email)             { return jpa.existsByEmail(email); }
    @Override public boolean existsByPhone(String phone)             { return jpa.existsByPhone(phone); }
}