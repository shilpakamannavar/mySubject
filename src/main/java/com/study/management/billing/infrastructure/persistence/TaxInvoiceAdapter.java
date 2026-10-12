package com.study.management.billing.infrastructure.persistence;

import com.study.management.billing.domain.model.TaxInvoice;
import com.study.management.billing.domain.model.TaxInvoiceSequence;
import com.study.management.billing.domain.model.TaxInvoiceStatus;
import com.study.management.billing.domain.port.out.TaxInvoicePort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Component
public class TaxInvoiceAdapter implements TaxInvoicePort {

    private final TaxInvoiceRepository         invoiceRepo;
    private final TaxInvoiceSequenceRepository sequenceRepo;

    public TaxInvoiceAdapter(TaxInvoiceRepository         invoiceRepo,
                             TaxInvoiceSequenceRepository sequenceRepo) {
        this.invoiceRepo  = invoiceRepo;
        this.sequenceRepo = sequenceRepo;
    }

    @Override
    public TaxInvoice save(TaxInvoice invoice) {
        return invoiceRepo.save(invoice);
    }

    @Override
    public Optional<TaxInvoice> findByInvoiceNumber(String invoiceNumber) {
        return invoiceRepo.findByInvoiceNumber(invoiceNumber);
    }

    @Override
    public Optional<TaxInvoice> findByRazorpayOrderId(String orderId) {
        return invoiceRepo.findByRazorpayOrderId(orderId);
    }

    @Override
    public List<TaxInvoice> findPaidByYearMonth(int year, int month) {
        LocalDate from = LocalDate.of(year, month, 1);
        LocalDate to   = from.withDayOfMonth(from.lengthOfMonth());
        return invoiceRepo.findByStatusAndDateBetween(TaxInvoiceStatus.PAID, from, to);
    }

    /**
     * Allocates the next sequential invoice number for the current calendar month.
     * Uses a pessimistic write lock on the sequence row to prevent duplicates under concurrency.
     * Format: TI/{YYYY-MM}/{NNNN}  e.g. TI/2026-09/0007
     */
    @Override
    @Transactional
    public String nextInvoiceNumber() {
        String yearMonth = YearMonth.now().toString(); // e.g. "2026-09"

        TaxInvoiceSequence seq = sequenceRepo.findByYearMonthForUpdate(yearMonth)
                .orElseGet(() -> sequenceRepo.save(new TaxInvoiceSequence(yearMonth)));

        int current = seq.getNextSeq();
        seq.setNextSeq(current + 1);
        sequenceRepo.save(seq);

        return String.format("TI/%s/%04d", yearMonth, current);
    }
}
