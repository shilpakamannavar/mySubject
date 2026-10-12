package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.Study;
import com.study.management.domain.model.StudyStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Internal Spring Data repository — package-visible to StudyPersistenceAdapter only.
 * @EntityGraph ensures visits + invoice are loaded eagerly so Jackson can
 * serialize responses without an open Hibernate session.
 */
public interface JpaStudyRepository extends JpaRepository<Study, Long> {

    @EntityGraph(attributePaths = {"visits", "invoice", "customer"})
    List<Study> findByStatus(StudyStatus status);

    @Override
    @EntityGraph(attributePaths = {"visits", "invoice", "customer"})
    Optional<Study> findById(Long id);

    boolean existsByStudyId(String studyId);

    @EntityGraph(attributePaths = {"visits", "invoice", "customer"})
    Optional<Study> findByStudyId(String studyId);

    @EntityGraph(attributePaths = {"visits", "invoice", "customer"})
    Optional<Study> findByInvoice_PaymentReference(String paymentReference);

    @Override
    @EntityGraph(attributePaths = {"visits", "invoice", "customer"})
    List<Study> findAll();

    @EntityGraph(attributePaths = {"visits", "invoice", "customer"})
    List<Study> findByCreatedByPhone(String phone);
}