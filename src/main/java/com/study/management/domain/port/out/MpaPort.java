package com.study.management.domain.port.out;

import com.study.management.domain.model.Mpa;
import com.study.management.domain.model.MpaStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MpaPort {
    Mpa save(Mpa mpa);
    Optional<Mpa> findById(UUID mpaId);
    List<Mpa> findAll();
    List<Mpa> findByStatus(MpaStatus status);
    boolean existsByMpaNumber(String mpaNumber);
    long countCenters(UUID mpaId);
}
