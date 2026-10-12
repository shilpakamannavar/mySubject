package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.PatientEntity;
import com.study.management.domain.model.PatientStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

interface JpaPatientRepository extends JpaRepository<PatientEntity, UUID> {
    Optional<PatientEntity> findByPatientNumber(String patientNumber);
    Optional<PatientEntity> findByPhone(String phone);
    Optional<PatientEntity> findByEmailIgnoreCase(String email);
    Page<PatientEntity> findByStatus(PatientStatus status, Pageable pageable);
    long countByPatientNumberStartingWith(String prefix);

    @Query("SELECT p FROM PatientEntity p WHERE " +
           "(:search IS NULL OR p.patientNumber LIKE %:search% OR p.initials LIKE %:search%) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<PatientEntity> searchPatients(@Param("search") String search,
                                       @Param("status") PatientStatus status,
                                       Pageable pageable);
}
