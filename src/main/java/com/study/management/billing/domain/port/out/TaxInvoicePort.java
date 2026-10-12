package com.study.management.billing.domain.port.out;

import com.study.management.billing.domain.model.TaxInvoice;

import java.util.List;
import java.util.Optional;

public interface TaxInvoicePort {

    TaxInvoice save(TaxInvoice invoice);

    Optional<TaxInvoice> findByInvoiceNumber(String invoiceNumber);

    Optional<TaxInvoice> findByRazorpayOrderId(String orderId);

    /** Returns all PAID invoices whose invoice_date falls within the given year-month. */
    List<TaxInvoice> findPaidByYearMonth(int year, int month);

    /**
     * Allocates the next sequential invoice number for the current calendar month.
     * The sequence is database-backed and pessimistically locked to guarantee uniqueness.
     * Format: TI/{YYYY-MM}/{NNNN}   e.g. TI/2026-09/0007
     */
    String nextInvoiceNumber();
}
