package com.study.management.domain.port.in;

import com.study.management.domain.model.Mpa;
import com.study.management.domain.model.MpaStatus;

import java.util.List;
import java.util.UUID;

public interface MpaUseCase {
    Mpa createMpa(Mpa mpa);
    Mpa getById(UUID mpaId);
    List<Mpa> listAll(MpaStatus status);
    Mpa update(UUID mpaId, Mpa updates);
}
