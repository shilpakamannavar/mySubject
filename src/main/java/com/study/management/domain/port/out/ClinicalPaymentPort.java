package com.study.management.domain.port.out;

import com.study.management.domain.model.ClinicalPaymentStatus;
import com.study.management.domain.model.PaymentEntity;
import com.study.management.domain.model.StudyEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClinicalPaymentPort {
    PaymentEntity save(PaymentEntity payment);
    Optional<PaymentEntity> findById(UUID paymentId);
    List<PaymentEntity> findByStudy(StudyEntity study);
    List<PaymentEntity> findByStudyAndStatus(StudyEntity study, ClinicalPaymentStatus status);
}
