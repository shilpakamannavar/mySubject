package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface JpaSubjectRepository extends JpaRepository<Subject, UUID> {
    Optional<Subject> findByEmailIgnoreCase(String email);
    boolean existsByEmail(String email);
    Optional<Subject> findByPhone(String phone);
    boolean existsByPhone(String phone);
    Optional<Subject> findByQrToken(String qrToken);
}