package com.study.management.application;

import com.study.management.domain.model.PatientStudyLog;
import com.study.management.domain.model.PatientStudyLogStatus;
import com.study.management.domain.model.StudyEnrollment;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.exception.BusinessRuleException;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.domain.port.in.PatientStudyLogUseCase;
import com.study.management.domain.port.out.ClinicalStudyPort;
import com.study.management.domain.port.out.EnrollmentPort;
import com.study.management.domain.port.out.PatientStudyLogPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PatientStudyLogApplicationService implements PatientStudyLogUseCase {

    private final PatientStudyLogPort logPort;
    private final EnrollmentPort enrollmentPort;
    private final ClinicalStudyPort studyPort;

    public PatientStudyLogApplicationService(PatientStudyLogPort logPort,
                                              EnrollmentPort enrollmentPort,
                                              ClinicalStudyPort studyPort) {
        this.logPort = logPort;
        this.enrollmentPort = enrollmentPort;
        this.studyPort = studyPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientStudyLog> getLogsByEnrollment(UUID studyId, UUID enrollmentId) {
        StudyEntity study = studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));

        StudyEnrollment enrollment = enrollmentPort.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));

        if (!enrollment.getStudy().getStudyId().equals(studyId)) {
            throw new BusinessRuleException(
                    "Enrollment " + enrollmentId + " does not belong to study " + studyId);
        }

        return logPort.findByEnrollmentOrderByVisitNumber(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientStudyLog> getLogsByStudy(UUID studyId) {
        StudyEntity study = studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));
        return logPort.findByStudyOrderByVisitNumber(study);
    }

    @Override
    public PatientStudyLog recordActualVisit(String logId, LocalDate actualDate) {
        PatientStudyLog log = logPort.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("PatientStudyLog", logId));

        if (log.isReadOnly()) {
            throw new BusinessRuleException("Study is inactive. Log is read-only.");
        }

        log.setActualVisitDate(actualDate);

        LocalDate windowOpen  = log.getWindowOpenDate();
        LocalDate windowClose = log.getWindowCloseDate();
        boolean inWindow = (windowOpen == null || !actualDate.isBefore(windowOpen))
                        && (windowClose == null || !actualDate.isAfter(windowClose));
        log.setVisitStatus(inWindow ? PatientStudyLogStatus.COMPLETED : PatientStudyLogStatus.OUT_OF_WINDOW);

        return logPort.save(log);
    }
}
