package com.study.management.billing.infrastructure.persistence;

import com.study.management.billing.domain.model.TaxInvoice;
import com.study.management.billing.domain.model.TaxInvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaxInvoiceRepository extends JpaRepository<TaxInvoice, Long> {

    Optional<TaxInvoice> findByInvoiceNumber(String invoiceNumber);

    Optional<TaxInvoice> findByRazorpayOrderId(String razorpayOrderId);

    @Query("SELECT t FROM TaxInvoice t WHERE t.status = :status " +
           "AND t.invoiceDate >= :from AND t.invoiceDate <= :to")
    List<TaxInvoice> findByStatusAndDateBetween(
            @Param("status") TaxInvoiceStatus status,
            @Param("from")   LocalDate        from,
            @Param("to")     LocalDate        to);
}
