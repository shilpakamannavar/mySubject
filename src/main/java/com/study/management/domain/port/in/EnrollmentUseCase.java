package com.study.management.domain.port.in;

import com.study.management.domain.model.StudyEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EnrollmentUseCase {
    StudyEnrollment enrollPatient(UUID studyId, StudyEnrollment enrollment);
    Page<StudyEnrollment> listEnrollments(UUID studyId, Pageable pageable);
    StudyEnrollment withdrawPatient(UUID studyId, UUID enrollmentId, StudyEnrollment updates);

    record EnrollmentEligibility(boolean eligible, String reason, String studyStatus,
                                  boolean subscriptionPaid, String subscriptionExpiry) {}

    /** Returns what is blocking enrollment without actually enrolling. Safe to call anytime. */
    EnrollmentEligibility checkEnrollmentEligibility(UUID studyId);

    /** Subject calls this after login to see all studies they have been invited/enrolled into. */
    List<StudyEnrollment> getMyEnrollments(String phoneNumber);

    /**
     * Subject accepts their pending enrollment — records informed consent and transitions
     * status from CONSENT_PENDING → ENROLLED.
     */
    StudyEnrollment acceptEnrollment(UUID enrollmentId, String subjectPhone,
                                     LocalDate consentDate, String consentVersion);
}
