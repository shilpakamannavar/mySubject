package com.study.management.domain.port.out;

import com.study.management.domain.model.Study;
import com.study.management.domain.model.StudyStatus;

import java.util.List;
import java.util.Optional;

/**
 * OUTPUT PORT — study persistence contract.
 * Implemented by StudyPersistenceAdapter in the infrastructure layer.
 */
public interface StudyPort {

    Optional<Study> findById(Long id);

    Optional<Study> findByStudyId(String studyId);

    /** Used by Razorpay webhook to look up the study from the Razorpay orderId. */
    Optional<Study> findByInvoicePaymentReference(String paymentReference);

    List<Study> findByStatus(StudyStatus status);

    List<Study> findAll();

    Study save(Study study);

    boolean existsByStudyId(String studyId);
}