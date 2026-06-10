package com.study.management.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id")
    private UUID paymentId;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private StudyEntity study;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private ClinicalPaymentType paymentType;

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "tax_amount", precision = 18, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "total_amount", precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "rate_per_service", precision = 18, scale = 2)
    private BigDecimal ratePerService;

    @Column(name = "gst_percentage", precision = 5, scale = 2)
    private BigDecimal gstPercentage;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "payment_gateway_ref")
    private String paymentGatewayRef;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private ClinicalPaymentStatus paymentStatus;

    @Column(name = "payment_url")
    private String paymentUrl;

    @Column(name = "paid_at")
    private OffsetDateTime paidAt;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "visit_id")
    private PatientVisit visit;

    @ManyToOne
    @JoinColumn(name = "enrollment_id")
    private StudyEnrollment enrollment;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (updatedAt == null) updatedAt = OffsetDateTime.now();
        if (paymentStatus == null) paymentStatus = ClinicalPaymentStatus.PENDING;
        if (currency == null) currency = "USD";
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // ── Getters & Setters ──────────────────────────────────────────────

    public UUID getPaymentId()                          { return paymentId; }
    public void setPaymentId(UUID v)                   { this.paymentId = v; }

    public StudyEntity getStudy()                       { return study; }
    public void setStudy(StudyEntity study)            { this.study = study; }

    public ClinicalPaymentType getPaymentType()         { return paymentType; }
    public void setPaymentType(ClinicalPaymentType v)  { this.paymentType = v; }

    public BigDecimal getAmount()                       { return amount; }
    public void setAmount(BigDecimal amount)           { this.amount = amount; }

    public String getCurrency()                         { return currency; }
    public void setCurrency(String currency)           { this.currency = currency; }

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

    public PatientVisit getVisit()                      { return visit; }
    public void setVisit(PatientVisit visit)           { this.visit = visit; }

    public StudyEnrollment getEnrollment()              { return enrollment; }
    public void setEnrollment(StudyEnrollment v)       { this.enrollment = v; }

    public String getNotes()                            { return notes; }
    public void setNotes(String notes)                 { this.notes = notes; }

    public OffsetDateTime getCreatedAt()                { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)         { this.createdAt = v; }

    public OffsetDateTime getUpdatedAt()                { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime v)         { this.updatedAt = v; }
}
