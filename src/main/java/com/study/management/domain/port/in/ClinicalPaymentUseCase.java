package com.study.management.domain.port.in;

import com.study.management.domain.model.PaymentEntity;

import java.util.List;
import java.util.UUID;

public interface ClinicalPaymentUseCase {
    PaymentEntity createPayment(UUID studyId, PaymentEntity payment);
    PaymentEntity handleWebhook(UUID paymentId, String gatewayRef, String status);
    List<PaymentEntity> getPaymentsForStudy(UUID studyId);
}
