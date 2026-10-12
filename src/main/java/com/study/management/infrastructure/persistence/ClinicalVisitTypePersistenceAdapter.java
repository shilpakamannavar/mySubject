package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.ClinicalVisitTypeEntity;
import com.study.management.domain.port.out.ClinicalVisitTypePort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClinicalVisitTypePersistenceAdapter implements ClinicalVisitTypePort {

    private final JpaClinicalVisitTypeRepository repository;

    public ClinicalVisitTypePersistenceAdapter(JpaClinicalVisitTypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ClinicalVisitTypeEntity> findAllActive() {
        return repository.findByIsActiveTrueOrderBySortOrderAsc();
    }

    @Override
    public List<ClinicalVisitTypeEntity> findAll() {
        return repository.findAll();
    }
}
