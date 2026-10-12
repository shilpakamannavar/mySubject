package com.study.management.billing.infrastructure.persistence;

import com.study.management.billing.domain.model.TaxInvoiceSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TaxInvoiceSequenceRepository extends JpaRepository<TaxInvoiceSequence, String> {

    /** Pessimistic write lock ensures no two concurrent checkouts get the same invoice number. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM TaxInvoiceSequence s WHERE s.yearMonth = :yearMonth")
    Optional<TaxInvoiceSequence> findByYearMonthForUpdate(@Param("yearMonth") String yearMonth);
}
