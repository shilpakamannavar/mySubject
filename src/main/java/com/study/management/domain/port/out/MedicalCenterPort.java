package com.study.management.domain.port.out;

import com.study.management.domain.model.CenterStatus;
import com.study.management.domain.model.MedicalCenter;
import com.study.management.domain.model.Mpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicalCenterPort {
    MedicalCenter save(MedicalCenter center);
    Optional<MedicalCenter> findById(UUID centerId);
    List<MedicalCenter> findByMpa(Mpa mpa);
    List<MedicalCenter> findByMpaAndStatus(Mpa mpa, CenterStatus status);
    long countByMpa(Mpa mpa);
}
