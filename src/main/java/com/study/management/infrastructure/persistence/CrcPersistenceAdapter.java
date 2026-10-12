package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.ClinicalResearchCoordinator;
import com.study.management.domain.port.out.CrcPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CrcPersistenceAdapter implements CrcPort {

    private final JpaCrcRepository jpa;

    public CrcPersistenceAdapter(JpaCrcRepository jpa) {
        this.jpa = jpa;
    }

    @Override public ClinicalResearchCoordinator save(ClinicalResearchCoordinator crc)    { return jpa.save(crc); }
    @Override public Optional<ClinicalResearchCoordinator> findById(UUID id)              { return jpa.findById(id); }
    @Override public List<ClinicalResearchCoordinator> findAll()                          { return jpa.findAll(); }
    @Override public Optional<ClinicalResearchCoordinator> findByPhone(String phone)      { return jpa.findByPhone(phone); }
    @Override public Optional<ClinicalResearchCoordinator> findByEmail(String email)      { return jpa.findByEmailIgnoreCase(email); }
    @Override public boolean existsByEmail(String email)                                   { return jpa.existsByEmail(email); }
    @Override public boolean existsByPhone(String phone)                                   { return jpa.existsByPhone(phone); }
}