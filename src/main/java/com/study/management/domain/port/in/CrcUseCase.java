package com.study.management.domain.port.in;

import com.study.management.domain.model.ClinicalResearchCoordinator;

import java.util.List;
import java.util.UUID;

public interface CrcUseCase {
    ClinicalResearchCoordinator create(ClinicalResearchCoordinator crc);
    ClinicalResearchCoordinator getById(UUID id);
    List<ClinicalResearchCoordinator> listAll();
}