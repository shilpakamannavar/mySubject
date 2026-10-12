package com.study.management.domain.port.in;

import com.study.management.domain.model.ClinicalVisitTypeEntity;

import java.util.List;

public interface ClinicalVisitTypeUseCase {
    List<ClinicalVisitTypeEntity> getAllActive();
    List<ClinicalVisitTypeEntity> getAll();
}
