package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.ClinicalPaymentType;
import com.study.management.domain.model.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreatePaymentRequest {

    private ClinicalPaymentType paymentType;
    private BigDecimal ratePerService;
    private BigDecimal gstPercentage;
    private String currency;
    private PaymentMethod paymentMethod;
    private String invoiceNumber;
    private LocalDate dueDate;
    private String notes;

    public ClinicalPaymentType getPaymentType()     { return paymentType; }
    public void setPaymentType(ClinicalPaymentType v) { this.paymentType = v; }
    public BigDecimal getRatePerService()            { return ratePerService; }
    public void setRatePerService(BigDecimal v)     { this.ratePerService = v; }
    public BigDecimal getGstPercentage()             { return gstPercentage; }
    public void setGstPercentage(BigDecimal v)      { this.gstPercentage = v; }
    public String getCurrency()                      { return currency; }
    public void setCurrency(String v)               { this.currency = v; }
    public PaymentMethod getPaymentMethod()          { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod v)   { this.paymentMethod = v; }
    public String getInvoiceNumber()                 { return invoiceNumber; }
    public void setInvoiceNumber(String v)          { this.invoiceNumber = v; }
    public LocalDate getDueDate()                    { return dueDate; }
    public void setDueDate(LocalDate v)             { this.dueDate = v; }
    public String getNotes()                         { return notes; }
    public void setNotes(String v)                  { this.notes = v; }
}
