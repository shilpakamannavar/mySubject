package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.PiStatus;
import com.study.management.domain.model.PrimaryInvestigator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface JpaPrimaryInvestigatorRepository extends JpaRepository<PrimaryInvestigator, UUID> {
    Optional<PrimaryInvestigator> findByPiNumber(String piNumber);
    List<PrimaryInvestigator> findByStatus(PiStatus status);
    Optional<PrimaryInvestigator> findByEmailIgnoreCase(String email);
    Optional<PrimaryInvestigator> findByPhone(String phone);
    boolean existsByEmail(String email);
}
