package com.study.management.domain.port.in;

import com.study.management.domain.model.Subject;

import java.util.List;
import java.util.UUID;

public interface SubjectUseCase {
    Subject create(Subject subject);
    Subject getById(UUID id);
    List<Subject> listAll();

    /** Generates (or refreshes) a QR token for the subject identified by phone. Token is valid for 24 hours. */
    Subject generateQrToken(String phone);

    /** Activates the subject whose QR token matches. Token must not be expired. One-time use — token is cleared after activation. */
    Subject activateByQrToken(String qrToken, String activatedByPhone);

    /** Deactivates the subject with the given ID. */
    Subject deactivate(UUID subjectId, String deactivatedByPhone);

    /** Links an existing subject to their patient record. Sets subject.patientId explicitly. */
    Subject linkToPatient(UUID subjectId, UUID patientId);
}