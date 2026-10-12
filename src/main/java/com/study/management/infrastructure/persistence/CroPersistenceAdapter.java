package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.ContractResearchOrganization;
import com.study.management.domain.port.out.CroPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CroPersistenceAdapter implements CroPort {

    private final JpaCroRepository jpa;

    public CroPersistenceAdapter(JpaCroRepository jpa) {
        this.jpa = jpa;
    }

    @Override public ContractResearchOrganization save(ContractResearchOrganization cro)    { return jpa.save(cro); }
    @Override public Optional<ContractResearchOrganization> findById(UUID id)               { return jpa.findById(id); }
    @Override public List<ContractResearchOrganization> findAll()                           { return jpa.findAll(); }
    @Override public Optional<ContractResearchOrganization> findByPhone(String phone)       { return jpa.findByPhone(phone); }
    @Override public Optional<ContractResearchOrganization> findByEmail(String email)       { return jpa.findByEmailIgnoreCase(email); }
    @Override public boolean existsByEmail(String email)                                     { return jpa.existsByEmail(email); }
    @Override public boolean existsByPhone(String phone)                                     { return jpa.existsByPhone(phone); }
}