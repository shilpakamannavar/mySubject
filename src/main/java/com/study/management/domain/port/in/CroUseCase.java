package com.study.management.domain.port.in;

import com.study.management.domain.model.ContractResearchOrganization;

import java.util.List;
import java.util.UUID;

public interface CroUseCase {
    ContractResearchOrganization create(ContractResearchOrganization cro);
    ContractResearchOrganization getById(UUID id);
    List<ContractResearchOrganization> listAll();
}