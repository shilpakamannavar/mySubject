package com.study.management.domain.port.out;

import com.study.management.domain.model.ClinicalVisitTypeEntity;

import java.util.List;

public interface ClinicalVisitTypePort {
    List<ClinicalVisitTypeEntity> findAllActive();
    List<ClinicalVisitTypeEntity> findAll();
}
