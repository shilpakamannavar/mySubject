package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.ClinicalPaymentStatus;
import com.study.management.domain.model.ClinicalPaymentType;
import com.study.management.domain.model.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class PaymentResponse {

    private UUID paymentId;
    private UUID studyId;
    private String studyName;
    private ClinicalPaymentType paymentType;
    private BigDecimal amount;
    private String currency;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal ratePerService;
    private BigDecimal gstPercentage;
    private PaymentMethod paymentMethod;
    private String paymentGatewayRef;
    private String invoiceNumber;
    private ClinicalPaymentStatus paymentStatus;
    private String paymentUrl;
    private OffsetDateTime paidAt;
    private LocalDate dueDate;
    private String notes;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public UUID getPaymentId()                          { return paymentId; }
    public void setPaymentId(UUID v)                   { this.paymentId = v; }
    public UUID getStudyId()                            { return studyId; }
    public void setStudyId(UUID v)                     { this.studyId = v; }
    public String getStudyName()                        { return studyName; }
    public void setStudyName(String v)                 { this.studyName = v; }
    public ClinicalPaymentType getPaymentType()         { return paymentType; }
    public void setPaymentType(ClinicalPaymentType v)  { this.paymentType = v; }
    public BigDecimal getAmount()                       { return amount; }
    public void setAmount(BigDecimal v)                { this.amount = v; }
    public String getCurrency()                         { return currency; }
    public void setCurrency(String v)                  { this.currency = v; }
    public BigDecimal getTaxAmount()                    { return taxAmount; }
    public void setTaxAmount(BigDecimal v)             { this.taxAmount = v; }
    public BigDecimal getTotalAmount()                  { return totalAmount; }
    public void setTotalAmount(BigDecimal v)           { this.totalAmount = v; }
    public BigDecimal getRatePerService()               { return ratePerService; }
    public void setRatePerService(BigDecimal v)        { this.ratePerService = v; }
    public BigDecimal getGstPercentage()                { return gstPercentage; }
    public void setGstPercentage(BigDecimal v)         { this.gstPercentage = v; }
    public PaymentMethod getPaymentMethod()             { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod v)      { this.paymentMethod = v; }
    public String getPaymentGatewayRef()                { return paymentGatewayRef; }
    public void setPaymentGatewayRef(String v)         { this.paymentGatewayRef = v; }
    public String getInvoiceNumber()                    { return invoiceNumber; }
    public void setInvoiceNumber(String v)             { this.invoiceNumber = v; }
    public ClinicalPaymentStatus getPaymentStatus()     { return paymentStatus; }
    public void setPaymentStatus(ClinicalPaymentStatus v) { this.paymentStatus = v; }
    public String getPaymentUrl()                       { return paymentUrl; }
    public void setPaymentUrl(String v)                { this.paymentUrl = v; }
    public OffsetDateTime getPaidAt()                   { return paidAt; }
    public void setPaidAt(OffsetDateTime v)            { this.paidAt = v; }
    public LocalDate getDueDate()                       { return dueDate; }
    public void setDueDate(LocalDate v)                { this.dueDate = v; }
    public String getNotes()                            { return notes; }
    public void setNotes(String v)                     { this.notes = v; }
    public OffsetDateTime getCreatedAt()                { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)         { this.createdAt = v; }
    public OffsetDateTime getUpdatedAt()                { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime v)         { this.updatedAt = v; }
}
