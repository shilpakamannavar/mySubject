package com.study.management.domain.port.in;

import com.study.management.domain.model.Study;
import com.study.management.domain.model.StudyVisit;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * INPUT PORT — study lifecycle operations.
 * Controllers depend on this interface, never on application service classes.
 */
public interface StudyUseCase {

    /** Step 1: save header + auto-generate visit rows → DRAFT.
     *  customerId is optional; when supplied the study is linked to that customer. */
    Study createStudy(Study study, Long customerId);

    /** Step 2: user fills visit table columns (visitNumber is locked) */
    Study updateVisits(Long studyId, List<StudyVisit> updatedVisits);

    Study getById(Long id);

    /** Used by Razorpay webhook to resolve a study from its Razorpay orderId. */
    Study getByPaymentReference(String paymentReference);

    /** "My Studies" screen — only ACTIVE studies */
    List<Study> getActiveStudies();

    /** All studies regardless of status */
    List<Study> getAllStudies();

    /** Activate a study (requires isPaid / payment completed) */
    Study activateStudy(Long id);

    /** Disable a study and revoke access */
    Study disableStudy(Long id);

    /** Fee breakdown: baseFee, gstAmount, totalAmount, breakdown string */
    Map<String, Object> calculateFee(Long id);
}