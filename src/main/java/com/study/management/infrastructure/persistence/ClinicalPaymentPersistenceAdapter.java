package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.ClinicalPaymentStatus;
import com.study.management.domain.model.PaymentEntity;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.port.out.ClinicalPaymentPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ClinicalPaymentPersistenceAdapter implements ClinicalPaymentPort {

    private final JpaClinicalPaymentRepository jpa;

    public ClinicalPaymentPersistenceAdapter(JpaClinicalPaymentRepository jpa) {
        this.jpa = jpa;
    }

    @Override public PaymentEntity save(PaymentEntity p)                    { return jpa.save(p); }
    @Override public Optional<PaymentEntity> findById(UUID id)              { return jpa.findById(id); }
    @Override public List<PaymentEntity> findByStudy(StudyEntity s)         { return jpa.findByStudy(s); }
    @Override public List<PaymentEntity> findByStudyAndStatus(StudyEntity s, ClinicalPaymentStatus st) {
        return jpa.findByStudyAndPaymentStatus(s, st);
    }
}
