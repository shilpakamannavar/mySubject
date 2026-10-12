package com.study.management.application;

import com.study.management.domain.model.ClinicalStudyStatus;
import com.study.management.domain.model.DurationUnit;
import com.study.management.domain.model.EnrollmentStatus;
import com.study.management.domain.model.PatientEntity;
import com.study.management.domain.model.PatientStudyLog;
import com.study.management.domain.model.PatientStudyLogStatus;
import com.study.management.domain.model.PatientVisit;
import com.study.management.domain.model.StudyEnrollment;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.StudyPlanVisit;
import com.study.management.domain.model.VisitStatus;
import com.study.management.domain.model.exception.BusinessRuleException;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.domain.port.in.EnrollmentUseCase;
import com.study.management.domain.port.in.StudySubscriptionUseCase;
import com.study.management.domain.port.out.ClinicalStudyPort;
import com.study.management.domain.port.out.EnrollmentPort;
import com.study.management.domain.port.out.PatientPort;
import com.study.management.domain.port.out.PatientStudyLogPort;
import com.study.management.domain.port.out.StudyPlanPort;
import com.study.management.domain.port.out.SubjectPort;
import com.study.management.domain.model.PatientGender;
import com.study.management.domain.model.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class EnrollmentApplicationService implements EnrollmentUseCase {

    private final EnrollmentPort enrollmentPort;
    private final ClinicalStudyPort studyPort;
    private final PatientPort patientPort;
    private final StudyPlanPort planPort;
    private final PatientStudyLogPort patientStudyLogPort;
    private final StudySubscriptionUseCase subscriptionUseCase;
    private final SubjectPort subjectPort;

    public EnrollmentApplicationService(EnrollmentPort enrollmentPort,
                                         ClinicalStudyPort studyPort,
                                         PatientPort patientPort,
                                         StudyPlanPort planPort,
                                         PatientStudyLogPort patientStudyLogPort,
                                         StudySubscriptionUseCase subscriptionUseCase,
                                         SubjectPort subjectPort) {
        this.enrollmentPort = enrollmentPort;
        this.studyPort = studyPort;
        this.patientPort = patientPort;
        this.planPort = planPort;
        this.patientStudyLogPort = patientStudyLogPort;
        this.subscriptionUseCase = subscriptionUseCase;
        this.subjectPort = subjectPort;
    }

    @Override
    public StudyEnrollment enrollPatient(UUID studyId, StudyEnrollment enrollment) {
        StudyEntity study = studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));

        if (study.getStatus() != ClinicalStudyStatus.ACTIVE) {
            throw new BusinessRuleException("Cannot enroll patient in a study with status: " + study.getStatus());
        }

        if (!subscriptionUseCase.isCurrentMonthPaidForStudy(studyId)) {
            throw new BusinessRuleException(
                "Monthly subscription has not been paid for this study's Primary Investigator. " +
                "The SMO or Primary Investigator must pay the monthly subscription — " +
                "one payment covers all studies under that PI.");
        }

        PatientEntity patient = resolvePatient(enrollment);

        if (enrollmentPort.existsByStudyAndPatient(study, patient)) {
            throw new BusinessRuleException("Patient is already enrolled in this study.");
        }

        enrollment.setStudy(study);
        enrollment.setPatient(patient);
        enrollment.setEnrollmentStatus(EnrollmentStatus.ENROLLED);
        StudyEnrollment saved = enrollmentPort.save(enrollment);

        // Auto-generate patient visits and study log entries from study plan.
        // Base date = enrollmentDate (Day 0). Falls back to screeningDate, then today.
        List<StudyPlanVisit> planVisits = planPort.findByStudyOrdered(study);
        LocalDate baseDate = saved.getEnrollmentDate() != null ? saved.getEnrollmentDate()
                           : saved.getScreeningDate() != null ? saved.getScreeningDate()
                           : LocalDate.now();
        LocalDate today = LocalDate.now();

        for (StudyPlanVisit planVisit : planVisits) {
            int dayOffset = planVisit.getTargetDayOffset() != null ? planVisit.getTargetDayOffset() : 0;
            LocalDate scheduledDate = baseDate.plusDays(dayOffset);

            Integer before = planVisit.getWindowPeriodBefore();
            Integer after  = planVisit.getWindowPeriodAfter();
            LocalDate windowOpen  = before != null ? scheduledDate.minusDays(before) : scheduledDate;
            LocalDate windowClose = after  != null ? scheduledDate.plusDays(after)   : scheduledDate;

            // PatientVisit (appointment record)
            PatientVisit pv = new PatientVisit();
            pv.setEnrollment(saved);
            pv.setPlanVisit(planVisit);
            pv.setScheduledDate(scheduledDate);
            pv.setWindowOpenDate(windowOpen);
            pv.setWindowCloseDate(windowClose);
            boolean isUpcoming = !scheduledDate.isAfter(today.plusDays(7)) && !scheduledDate.isBefore(today);
            pv.setVisitStatus(isUpcoming ? VisitStatus.UPCOMING : VisitStatus.SCHEDULED);
            enrollmentPort.saveVisit(pv);

            // PatientStudyLog (subject's personal study plan row)
            PatientStudyLog log = new PatientStudyLog();
            String logId = "LOG-" + patient.getPatientNumber() + "-V" + planVisit.getVisitNumber();
            log.setLogId(logId);
            log.setPatientNumber(patient.getPatientNumber());
            log.setEnrollment(saved);
            log.setStudy(study);
            log.setPlanVisit(planVisit);
            log.setVisitNumber(planVisit.getVisitNumber());
            log.setVisitType(planVisit.getVisitType() != null ? planVisit.getVisitType().name() : null);
            log.setTargetScheduledDate(scheduledDate);
            log.setWindowOpenDate(windowOpen);
            log.setWindowCloseDate(windowClose);
            log.setVisitSpecificInstructions(planVisit.getPatientInstruction());
            log.setVisitStatus(PatientStudyLogStatus.SCHEDULED);
            log.setReadOnly(false);
            patientStudyLogPort.save(log);
        }

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentUseCase.EnrollmentEligibility checkEnrollmentEligibility(UUID studyId) {
        StudyEntity study = studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));

        if (study.getStatus() != ClinicalStudyStatus.ACTIVE) {
            return new EnrollmentUseCase.EnrollmentEligibility(
                    false,
                    "Study is not ACTIVE (current status: " + study.getStatus() + "). "
                    + "Ask the Primary Investigator or Admin to activate the study before enrolling subjects.",
                    study.getStatus().name(), false, null);
        }

        boolean subPaid = subscriptionUseCase.isCurrentMonthPaidForStudy(studyId);
        if (!subPaid) {
            return new EnrollmentUseCase.EnrollmentEligibility(
                    false,
                    "Monthly subscription is not paid for this study's Primary Investigator. "
                    + "Go to PI Subscriptions → Pay Subscription to unlock enrollment.",
                    study.getStatus().name(), false, null);
        }

        // Find expiry from active subscription
        String expiry = null;
        if (study.getCreatedByPi() != null) {
            var sub = subscriptionUseCase.getPiCurrentMonthSubscription(study.getCreatedByPi().getPiId());
            if (sub.getSubscriptionEndDate() != null) expiry = sub.getSubscriptionEndDate().toString();
        }

        return new EnrollmentUseCase.EnrollmentEligibility(true, "Eligible to enroll",
                study.getStatus().name(), true, expiry);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyEnrollment> getMyEnrollments(String phoneNumber) {
        // Prefer the explicit subject→patient FK; fall back to phone/email matching.
        Optional<Subject> subjectOpt = subjectPort.findByPhone(phoneNumber);
        if (subjectOpt.isPresent() && subjectOpt.get().getPatientId() != null) {
            PatientEntity patient = new PatientEntity();
            patient.setPatientId(subjectOpt.get().getPatientId());
            return enrollmentPort.findByPatient(patient);
        }
        PatientEntity patient = patientPort.findByPhone(phoneNumber)
                .or(() -> patientPort.findByEmail(phoneNumber))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No patient record found for phone: " + phoneNumber +
                        ". Ask your CRC to link your subject account to your patient record."));
        return enrollmentPort.findByPatient(patient);
    }

    @Override
    public StudyEnrollment acceptEnrollment(UUID enrollmentId, String subjectPhone,
                                             LocalDate consentDate, String consentVersion) {
        StudyEnrollment enrollment = enrollmentPort.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));

        // Verify this enrollment belongs to the calling subject
        PatientEntity patient = enrollment.getPatient();
        boolean isOwner = (patient.getPhone() != null && patient.getPhone().equals(subjectPhone))
                       || (patient.getEmail() != null && patient.getEmail().equals(subjectPhone));
        if (!isOwner) {
            throw new BusinessRuleException("Enrollment does not belong to this subject.");
        }

        if (enrollment.getEnrollmentStatus() == EnrollmentStatus.WITHDRAWN
                || enrollment.getEnrollmentStatus() == EnrollmentStatus.COMPLETED) {
            throw new BusinessRuleException("Cannot accept an enrollment with status: "
                    + enrollment.getEnrollmentStatus());
        }

        enrollment.setInformedConsentDate(consentDate != null ? consentDate : LocalDate.now());
        if (consentVersion != null) enrollment.setConsentVersion(consentVersion);
        enrollment.setEnrollmentStatus(EnrollmentStatus.ENROLLED);
        return enrollmentPort.save(enrollment);
    }

    /**
     * Resolves the PatientEntity for an enrollment.
     * Priority:
     *   1. subjectId supplied → find Subject → use its patientId FK (or resolve by phone/email, or auto-create)
     *   2. patientId supplied directly → look up patient by ID
     */
    private PatientEntity resolvePatient(StudyEnrollment enrollment) {
        if (enrollment.getSubjectId() != null) {
            Subject subject = subjectPort.findById(enrollment.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject", enrollment.getSubjectId()));

            // Use existing link if already set
            if (subject.getPatientId() != null) {
                return patientPort.findById(subject.getPatientId())
                        .orElseThrow(() -> new ResourceNotFoundException("Patient", subject.getPatientId()));
            }

            // Try to find by phone then email
            PatientEntity patient = null;
            if (subject.getPhone() != null) {
                patient = patientPort.findByPhone(subject.getPhone()).orElse(null);
            }
            if (patient == null && subject.getEmail() != null) {
                patient = patientPort.findByEmail(subject.getEmail()).orElse(null);
            }

            // Auto-create patient record from subject data
            if (patient == null) {
                PatientEntity newPatient = new PatientEntity();
                newPatient.setFirstName(subject.getFirstName());
                newPatient.setLastName(subject.getLastName());
                newPatient.setInitials(subject.getInitials());
                newPatient.setDateOfBirth(subject.getDateOfBirth());
                PatientGender gender = PatientGender.UNKNOWN;
                if (subject.getGender() != null) {
                    try { gender = PatientGender.valueOf(subject.getGender().toUpperCase()); }
                    catch (IllegalArgumentException ignored) {}
                }
                newPatient.setGender(gender);
                newPatient.setPhone(subject.getPhone());
                newPatient.setEmail(subject.getEmail());
                String today = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
                long count = patientPort.countByNumberPrefix("PAT-" + today);
                newPatient.setPatientNumber(String.format("PAT-%s-%03d", today, count + 1));
                patient = patientPort.save(newPatient);
            }

            // Persist the link so future calls don't repeat this lookup
            subject.setPatientId(patient.getPatientId());
            subjectPort.save(subject);
            return patient;
        }

        // Legacy path: patientId supplied directly
        UUID pid = (enrollment.getPatient() != null) ? enrollment.getPatient().getPatientId() : null;
        if (pid == null) throw new BusinessRuleException("Either subjectId or patientId must be provided.");
        return patientPort.findById(pid)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", pid));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudyEnrollment> listEnrollments(UUID studyId, Pageable pageable) {
        StudyEntity study = studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));
        return enrollmentPort.findByStudy(study, pageable);
    }

    @Override
    public StudyEnrollment withdrawPatient(UUID studyId, UUID enrollmentId, StudyEnrollment updates) {
        studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));
        StudyEnrollment enrollment = enrollmentPort.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));

        enrollment.setEnrollmentStatus(EnrollmentStatus.WITHDRAWN);
        if (updates.getWithdrawalDate() != null) enrollment.setWithdrawalDate(updates.getWithdrawalDate());
        if (updates.getWithdrawalReason() != null) enrollment.setWithdrawalReason(updates.getWithdrawalReason());

        // Cancel remaining SCHEDULED/UPCOMING visits
        List<VisitStatus> cancelStatuses = Arrays.asList(VisitStatus.SCHEDULED, VisitStatus.UPCOMING);
        List<PatientVisit> visits = enrollmentPort.findVisitsByEnrollmentAndStatusIn(enrollment, cancelStatuses);
        for (PatientVisit visit : visits) {
            visit.setVisitStatus(VisitStatus.CANCELLED);
            enrollmentPort.saveVisit(visit);
        }

        return enrollmentPort.save(enrollment);
    }
}
