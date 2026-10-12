package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface JpaHospitalRepository extends JpaRepository<Hospital, UUID> {
    Optional<Hospital> findByPhone(String phone);
    Optional<Hospital> findByEmailIgnoreCase(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}