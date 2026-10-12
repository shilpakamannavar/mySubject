package com.study.management.billing.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tax_invoice_sequence")
public class TaxInvoiceSequence {

    @Id
    @Column(name = "year_month", length = 7)
    private String yearMonth;

    @Column(name = "next_seq", nullable = false)
    private int nextSeq = 1;

    @Version
    @Column(name = "version")
    private int version;

    public TaxInvoiceSequence() {}

    public TaxInvoiceSequence(String yearMonth) {
        this.yearMonth = yearMonth;
        this.nextSeq   = 1;
    }

    public String getYearMonth()              { return yearMonth; }
    public void   setYearMonth(String v)      { this.yearMonth = v; }

    public int  getNextSeq()                  { return nextSeq; }
    public void setNextSeq(int v)             { this.nextSeq = v; }

    public int  getVersion()                  { return version; }
    public void setVersion(int v)             { this.version = v; }
}
