package com.study.management.domain.port.in;

import com.study.management.domain.model.CoInvestigator;

import java.util.List;
import java.util.UUID;

public interface CoInvestigatorUseCase {
    CoInvestigator create(CoInvestigator ci);
    CoInvestigator getById(UUID id);
    List<CoInvestigator> listAll();
}