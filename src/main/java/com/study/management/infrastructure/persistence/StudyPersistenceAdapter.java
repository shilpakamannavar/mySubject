package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.Study;
import com.study.management.domain.model.StudyStatus;
import com.study.management.domain.port.out.StudyPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * OUTPUT PORT ADAPTER — bridges the domain's StudyPort interface to JPA.
 * The domain layer never sees Spring Data; it only knows about StudyPort.
 */
@Repository
public class StudyPersistenceAdapter implements StudyPort {

    private final JpaStudyRepository jpa;

    public StudyPersistenceAdapter(JpaStudyRepository jpa) {
        this.jpa = jpa;
    }

    @Override public Optional<Study> findById(Long id)                              { return jpa.findById(id); }
    @Override public Optional<Study> findByStudyId(String sid)                     { return jpa.findByStudyId(sid); }
    @Override public Optional<Study> findByInvoicePaymentReference(String ref)      { return jpa.findByInvoice_PaymentReference(ref); }
    @Override public List<Study>     findByStatus(StudyStatus s)                    { return jpa.findByStatus(s); }
    @Override public List<Study>     findAll()                                      { return jpa.findAll(); }
    @Override public Study           save(Study study)                              { return jpa.save(study); }
    @Override public boolean         existsByStudyId(String sid)                    { return jpa.existsByStudyId(sid); }
}