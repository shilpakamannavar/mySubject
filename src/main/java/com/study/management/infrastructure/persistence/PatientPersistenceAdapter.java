package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.PatientEntity;
import com.study.management.domain.model.PatientStatus;
import com.study.management.domain.port.out.PatientPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PatientPersistenceAdapter implements PatientPort {

    private final JpaPatientRepository jpa;

    public PatientPersistenceAdapter(JpaPatientRepository jpa) {
        this.jpa = jpa;
    }

    @Override public PatientEntity save(PatientEntity p)            { return jpa.save(p); }
    @Override public Optional<PatientEntity> findById(UUID id)      { return jpa.findById(id); }
    @Override public long countByNumberPrefix(String prefix)        { return jpa.countByPatientNumberStartingWith(prefix); }
    @Override public Page<PatientEntity> search(String search, PatientStatus status, Pageable pageable) {
        return jpa.searchPatients(search, status, pageable);
    }
}
