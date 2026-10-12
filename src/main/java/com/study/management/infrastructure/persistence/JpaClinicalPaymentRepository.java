package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.ClinicalPaymentStatus;
import com.study.management.domain.model.PaymentEntity;
import com.study.management.domain.model.StudyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaClinicalPaymentRepository extends JpaRepository<PaymentEntity, UUID> {
    List<PaymentEntity> findByStudy(StudyEntity study);
    List<PaymentEntity> findByStudyAndPaymentStatus(StudyEntity study, ClinicalPaymentStatus status);
}
