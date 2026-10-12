package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.CenterStatus;
import com.study.management.domain.model.MedicalCenter;
import com.study.management.domain.model.Mpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaMedicalCenterRepository extends JpaRepository<MedicalCenter, UUID> {
    List<MedicalCenter> findByMpa(Mpa mpa);
    Page<MedicalCenter> findByMpa(Mpa mpa, Pageable pageable);
    List<MedicalCenter> findByMpaAndStatus(Mpa mpa, CenterStatus status);
    Page<MedicalCenter> findByMpaAndStatus(Mpa mpa, CenterStatus status, Pageable pageable);
    long countByMpa(Mpa mpa);
}
