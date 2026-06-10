package com.study.management.application;

import com.study.management.domain.model.*;
import com.study.management.domain.port.in.StudyUseCase;
import com.study.management.domain.port.out.CustomerPort;
import com.study.management.domain.port.out.StudyPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * APPLICATION SERVICE — implements StudyUseCase (input port).
 * Depends only on output-port interfaces; knows nothing about JPA, HTTP, or payment providers.
 */
@Service
@Transactional
public class StudyApplicationService implements StudyUseCase {

    private final StudyPort studyPort;
    private final CustomerPort customerPort;

    public StudyApplicationService(StudyPort studyPort, CustomerPort customerPort) {
        this.studyPort    = studyPort;
        this.customerPort = customerPort;
    }

    // ── Input port implementation ──────────────────────────────────────

    @Override
    public Study createStudy(Study study, Long customerId) {
        if (studyPort.existsByStudyId(study.getStudyId()))
            throw new IllegalArgumentException("Study ID already exists: " + study.getStudyId());

        if (customerId != null) {
            Customer customer = customerPort.findById(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));
            study.setCustomer(customer);
        }

        study.setStatus(StudyStatus.DRAFT);

        // Auto-generate one empty visit row per visit slot
        List<StudyVisit> visits = new ArrayList<>();
        for (int i = 1; i <= study.getTotalVisitsPerPatient(); i++) {
            StudyVisit visit = new StudyVisit();
            visit.setVisitNumber(i);
            visit.setStudy(study);
            visits.add(visit);
        }
        study.setVisits(visits);

        return studyPort.save(study);
    }

    @Override
    public Study updateVisits(Long studyId, List<StudyVisit> incoming) {
        Study study = getById(studyId);

        if (study.getStatus() != StudyStatus.DRAFT)
            throw new IllegalStateException(
                    "Visit details can only be edited while the study is in DRAFT status.");

        // Match by visitNumber and copy editable fields (visitNumber itself is locked)
        for (StudyVisit update : incoming) {
            study.getVisits().stream()
                    .filter(v -> v.getVisitNumber().equals(update.getVisitNumber()))
                    .findFirst()
                    .ifPresent(existing -> {
                        existing.setVisitType(update.getVisitType());
                        existing.setDurationType(update.getDurationType());
                        existing.setDurationValue(update.getDurationValue());
                        existing.setDurationFromLastVisitDays(update.getDurationFromLastVisitDays());
                        existing.setWindowMinusDays(update.getWindowMinusDays());
                        existing.setWindowPlusDays(update.getWindowPlusDays());
                        existing.setPatientInstructions(update.getPatientInstructions());
                    });
        }

        return studyPort.save(study);
    }

    @Override
    @Transactional(readOnly = true)
    public Study getById(Long id) {
        return studyPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Study not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Study getByPaymentReference(String paymentReference) {
        return studyPort.findByInvoicePaymentReference(paymentReference)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No study found for payment reference: " + paymentReference));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Study> getActiveStudies() {
        return studyPort.findByStatus(StudyStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Study> getAllStudies() {
        return studyPort.findAll();
    }

    @Override
    public Study activateStudy(Long id) {
        Study study = getById(id);
        Invoice invoice = study.getInvoice();
        if (invoice == null || invoice.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Payment required to activate study");
        }
        study.setStatus(StudyStatus.ACTIVE);
        return studyPort.save(study);
    }

    @Override
    public Study disableStudy(Long id) {
        Study study = getById(id);
        study.setStatus(StudyStatus.DISABLED);
        return studyPort.save(study);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> calculateFee(Long id) {
        Study study = getById(id);
        BigDecimal ratePerVisit = new BigDecimal("500.00");
        BigDecimal gstRate = new BigDecimal("0.18");

        int totalVisits = study.getNumberOfPatients() * study.getTotalVisitsPerPatient();
        BigDecimal baseFee = ratePerVisit.multiply(BigDecimal.valueOf(totalVisits));
        BigDecimal gstAmount = baseFee.multiply(gstRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = baseFee.add(gstAmount);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("studyId", study.getStudyId());
        result.put("baseFee", baseFee);
        result.put("gstAmount", gstAmount);
        result.put("totalAmount", totalAmount);
        result.put("breakdown", String.format("%d patients × %d visits × ₹%.0f/visit",
                study.getNumberOfPatients(), study.getTotalVisitsPerPatient(), ratePerVisit));
        return result;
    }
}