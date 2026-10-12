package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.ContractResearchOrganization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface JpaCroRepository extends JpaRepository<ContractResearchOrganization, UUID> {
    Optional<ContractResearchOrganization> findByPhone(String phone);
    Optional<ContractResearchOrganization> findByEmailIgnoreCase(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}