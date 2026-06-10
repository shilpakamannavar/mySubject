package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.Mpa;
import com.study.management.domain.model.MpaStatus;
import com.study.management.domain.port.out.MpaPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MpaPersistenceAdapter implements MpaPort {

    private final JpaMpaRepository jpa;
    private final JpaMedicalCenterRepository centerJpa;

    public MpaPersistenceAdapter(JpaMpaRepository jpa, JpaMedicalCenterRepository centerJpa) {
        this.jpa = jpa;
        this.centerJpa = centerJpa;
    }

    @Override public Mpa save(Mpa mpa)                              { return jpa.save(mpa); }
    @Override public Optional<Mpa> findById(UUID mpaId)            { return jpa.findById(mpaId); }
    @Override public List<Mpa> findAll()                           { return jpa.findAll(); }
    @Override public List<Mpa> findByStatus(MpaStatus status)      { return jpa.findByStatus(status); }
    @Override public boolean existsByMpaNumber(String num)         { return jpa.findByMpaNumber(num).isPresent(); }
    @Override public long countCenters(UUID mpaId) {
        return jpa.findById(mpaId).map(centerJpa::countByMpa).orElse(0L);
    }
}
