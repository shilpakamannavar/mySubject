package com.study.management.domain.port.out;

import com.study.management.domain.model.ContractResearchOrganization;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CroPort {
    ContractResearchOrganization save(ContractResearchOrganization cro);
    Optional<ContractResearchOrganization> findById(UUID id);
    List<ContractResearchOrganization> findAll();
    Optional<ContractResearchOrganization> findByPhone(String phone);
    Optional<ContractResearchOrganization> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
