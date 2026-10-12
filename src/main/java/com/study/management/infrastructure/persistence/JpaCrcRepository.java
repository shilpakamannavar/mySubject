package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.ClinicalResearchCoordinator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface JpaCrcRepository extends JpaRepository<ClinicalResearchCoordinator, UUID> {
    Optional<ClinicalResearchCoordinator> findByPhone(String phone);
    Optional<ClinicalResearchCoordinator> findByEmailIgnoreCase(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}