package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.Subject;
import com.study.management.domain.port.out.SubjectPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SubjectPersistenceAdapter implements SubjectPort {

    private final JpaSubjectRepository jpa;

    public SubjectPersistenceAdapter(JpaSubjectRepository jpa) {
        this.jpa = jpa;
    }

    @Override public Subject save(Subject subject)               { return jpa.save(subject); }
    @Override public Optional<Subject> findById(UUID id)         { return jpa.findById(id); }
    @Override public List<Subject> findAll()                     { return jpa.findAll(); }
    @Override public Optional<Subject> findByEmail(String email) { return jpa.findByEmailIgnoreCase(email); }
    @Override public boolean existsByEmail(String email)          { return jpa.existsByEmail(email); }
    @Override public Optional<Subject> findByPhone(String phone)       { return jpa.findByPhone(phone); }
    @Override public boolean existsByPhone(String phone)               { return jpa.existsByPhone(phone); }
    @Override public Optional<Subject> findByQrToken(String qrToken)   { return jpa.findByQrToken(qrToken); }
}