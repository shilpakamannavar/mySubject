package com.study.management.domain.port.in;

import com.study.management.domain.model.CenterStatus;
import com.study.management.domain.model.MedicalCenter;

import java.util.List;
import java.util.UUID;

public interface MedicalCenterUseCase {
    MedicalCenter createCenter(UUID mpaId, MedicalCenter center);
    List<MedicalCenter> listCentersForMpa(UUID mpaId, CenterStatus status);
    MedicalCenter getCenterDetail(UUID centerId);
}
