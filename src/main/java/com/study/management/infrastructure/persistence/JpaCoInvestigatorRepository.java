package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.CoInvestigator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface JpaCoInvestigatorRepository extends JpaRepository<CoInvestigator, UUID> {
    Optional<CoInvestigator> findByPhone(String phone);
    Optional<CoInvestigator> findByEmailIgnoreCase(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}