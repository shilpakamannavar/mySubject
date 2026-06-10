package com.study.management.domain.port.in;

import com.study.management.domain.model.StudyEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EnrollmentUseCase {
    StudyEnrollment enrollPatient(UUID studyId, StudyEnrollment enrollment);
    Page<StudyEnrollment> listEnrollments(UUID studyId, Pageable pageable);
    StudyEnrollment withdrawPatient(UUID studyId, UUID enrollmentId, StudyEnrollment updates);
}
