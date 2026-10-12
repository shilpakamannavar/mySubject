package com.study.management.domain.port.out;

import com.study.management.domain.model.ClinicalResearchCoordinator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CrcPort {
    ClinicalResearchCoordinator save(ClinicalResearchCoordinator crc);
    Optional<ClinicalResearchCoordinator> findById(UUID id);
    List<ClinicalResearchCoordinator> findAll();
    Optional<ClinicalResearchCoordinator> findByPhone(String phone);
    Optional<ClinicalResearchCoordinator> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
