package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.CoInvestigator;
import com.study.management.domain.port.out.CoInvestigatorPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CoInvestigatorPersistenceAdapter implements CoInvestigatorPort {

    private final JpaCoInvestigatorRepository jpa;

    public CoInvestigatorPersistenceAdapter(JpaCoInvestigatorRepository jpa) {
        this.jpa = jpa;
    }

    @Override public CoInvestigator save(CoInvestigator ci)               { return jpa.save(ci); }
    @Override public Optional<CoInvestigator> findById(UUID id)           { return jpa.findById(id); }
    @Override public List<CoInvestigator> findAll()                       { return jpa.findAll(); }
    @Override public Optional<CoInvestigator> findByPhone(String phone)   { return jpa.findByPhone(phone); }
    @Override public Optional<CoInvestigator> findByEmail(String email)   { return jpa.findByEmailIgnoreCase(email); }
    @Override public boolean existsByEmail(String email)                   { return jpa.existsByEmail(email); }
    @Override public boolean existsByPhone(String phone)                   { return jpa.existsByPhone(phone); }
}