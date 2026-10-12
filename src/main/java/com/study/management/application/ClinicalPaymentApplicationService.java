package com.study.management.application;

import com.study.management.domain.model.ClinicalPaymentStatus;
import com.study.management.domain.model.ClinicalPaymentType;
import com.study.management.domain.model.ClinicalStudyStatus;
import com.study.management.domain.model.PaymentEntity;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.domain.port.in.ClinicalPaymentUseCase;
import com.study.management.domain.port.out.ClinicalPaymentPort;
import com.study.management.domain.port.out.ClinicalStudyPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ClinicalPaymentApplicationService implements ClinicalPaymentUseCase {

    private final ClinicalPaymentPort paymentPort;
    private final ClinicalStudyPort studyPort;

    public ClinicalPaymentApplicationService(ClinicalPaymentPort paymentPort,
                                              ClinicalStudyPort studyPort) {
        this.paymentPort = paymentPort;
        this.studyPort = studyPort;
    }

    @Override
    public PaymentEntity createPayment(UUID studyId, PaymentEntity payment) {
        StudyEntity study = studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));
        payment.setStudy(study);

        // Calculate total: ratePerService * totalVisits * noOfPatients + GST
        BigDecimal rate = payment.getRatePerService() != null ? payment.getRatePerService() : BigDecimal.ZERO;
        Integer totalVisits = study.getTotalNumberOfVisits() != null ? study.getTotalNumberOfVisits() : 0;
        Integer patients = study.getNoOfPatientsToEnroll() != null ? study.getNoOfPatientsToEnroll() : 0;

        BigDecimal baseAmount = rate.multiply(BigDecimal.valueOf(totalVisits))
                .multiply(BigDecimal.valueOf(patients));
        payment.setAmount(baseAmount);

        BigDecimal gst = payment.getGstPercentage() != null ? payment.getGstPercentage() : BigDecimal.ZERO;
        BigDecimal taxAmount = baseAmount.multiply(gst).divide(BigDecimal.valueOf(100));
        payment.setTaxAmount(taxAmount);
        payment.setTotalAmount(baseAmount.add(taxAmount));

        payment.setPaymentStatus(ClinicalPaymentStatus.PENDING);
        PaymentEntity saved = paymentPort.save(payment);

        // Set payment URL
        saved.setPaymentUrl("https://pay.gateway.internal/checkout/" + saved.getPaymentId());
        return paymentPort.save(saved);
    }

    @Override
    public PaymentEntity handleWebhook(UUID paymentId, String gatewayRef, String status) {
        PaymentEntity payment = paymentPort.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        ClinicalPaymentStatus newStatus = ClinicalPaymentStatus.valueOf(status);
        payment.setPaymentStatus(newStatus);
        payment.setPaymentGatewayRef(gatewayRef);

        if (newStatus == ClinicalPaymentStatus.COMPLETED
                && payment.getPaymentType() == ClinicalPaymentType.STUDY_ACTIVATION) {
            payment.setPaidAt(OffsetDateTime.now());

            // Activate the study
            StudyEntity study = payment.getStudy();
            study.setStatus(ClinicalStudyStatus.ACTIVE);
            study.setActivatedAt(OffsetDateTime.now());
            studyPort.save(study);
        }

        return paymentPort.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentEntity> getPaymentsForStudy(UUID studyId) {
        StudyEntity study = studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));
        return paymentPort.findByStudy(study);
    }
}
