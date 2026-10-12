package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.ClinicalVisitTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaClinicalVisitTypeRepository extends JpaRepository<ClinicalVisitTypeEntity, String> {
    List<ClinicalVisitTypeEntity> findByIsActiveTrueOrderBySortOrderAsc();
}
