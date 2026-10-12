package com.study.management.domain.port.out;

import com.study.management.domain.model.Subject;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubjectPort {
    Subject save(Subject subject);
    Optional<Subject> findById(UUID id);
    List<Subject> findAll();
    Optional<Subject> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Subject> findByPhone(String phone);
    boolean existsByPhone(String phone);
    Optional<Subject> findByQrToken(String qrToken);
}