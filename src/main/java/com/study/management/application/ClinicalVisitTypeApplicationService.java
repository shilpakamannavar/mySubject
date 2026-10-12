package com.study.management.application;

import com.study.management.domain.model.ClinicalVisitTypeEntity;
import com.study.management.domain.port.in.ClinicalVisitTypeUseCase;
import com.study.management.domain.port.out.ClinicalVisitTypePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ClinicalVisitTypeApplicationService implements ClinicalVisitTypeUseCase {

    private final ClinicalVisitTypePort port;

    public ClinicalVisitTypeApplicationService(ClinicalVisitTypePort port) {
        this.port = port;
    }

    @Override
    public List<ClinicalVisitTypeEntity> getAllActive() {
        return port.findAllActive();
    }

    @Override
    public List<ClinicalVisitTypeEntity> getAll() {
        return port.findAll();
    }
}
