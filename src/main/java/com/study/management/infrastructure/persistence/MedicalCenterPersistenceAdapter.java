package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.CenterStatus;
import com.study.management.domain.model.MedicalCenter;
import com.study.management.domain.model.Mpa;
import com.study.management.domain.port.out.MedicalCenterPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MedicalCenterPersistenceAdapter implements MedicalCenterPort {

    private final JpaMedicalCenterRepository jpa;

    public MedicalCenterPersistenceAdapter(JpaMedicalCenterRepository jpa) {
        this.jpa = jpa;
    }

    @Override public MedicalCenter save(MedicalCenter c)                   { return jpa.save(c); }
    @Override public Optional<MedicalCenter> findById(UUID id)             { return jpa.findById(id); }
    @Override public List<MedicalCenter> findByMpa(Mpa mpa)                { return jpa.findByMpa(mpa); }
    @Override public List<MedicalCenter> findByMpaAndStatus(Mpa mpa, CenterStatus s) {
        return jpa.findByMpaAndStatus(mpa, s);
    }
    @Override public long countByMpa(Mpa mpa)                              { return jpa.countByMpa(mpa); }
}
