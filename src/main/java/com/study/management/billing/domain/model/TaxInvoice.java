package com.study.management.billing.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * GST-compliant B2B Tax Invoice (Section 31, CGST Act 2017).
 * Once status is PAID the record is immutable — corrections must use Credit/Debit Notes.
 */
@Entity
@Table(name = "tax_invoice")
public class TaxInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Sequential, tamper-proof: TI/{YYYY-MM}/{NNNN}. */
    @Column(name = "invoice_number", unique = true, nullable = false, updatable = false)
    private String invoiceNumber;

    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaxInvoiceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "tax_type", nullable = false)
    private TaxType taxType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "billing_profile_id", nullable = false)
    private GstBillingProfile billingProfile;

    @Column(name = "service_description", nullable = false)
    private String serviceDescription;

    /** SAC (Services Accounting Code) per the GST tariff. */
    @Column(name = "sac_code", length = 10, nullable = false)
    private String sacCode;

    @Column(name = "base_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal baseAmount;

    @Column(name = "cgst_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal cgstRate;

    @Column(name = "cgst_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal cgstAmount;

    @Column(name = "sgst_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal sgstRate;

    @Column(name = "sgst_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal sgstAmount;

    @Column(name = "igst_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal igstRate;

    @Column(name = "igst_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal igstAmount;

    @Column(name = "total_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "razorpay_order_id")
    private String razorpayOrderId;

    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId;

    /** 2-digit numeric GST Place of Supply code (from doctor's GSTIN). */
    @Column(name = "place_of_supply", length = 2, nullable = false)
    private String placeOfSupply;

    /** Number of subscription months covered by this invoice. */
    @Column(name = "subscription_months")
    private Integer subscriptionMonths = 1;

    /** First day of the subscription period this invoice covers. */
    @Column(name = "subscription_start_date")
    private LocalDate subscriptionStartDate;

    /** Last day of the subscription period (startDate + subscriptionMonths - 1 day). */
    @Column(name = "subscription_end_date")
    private LocalDate subscriptionEndDate;

    /** Optional back-reference: e.g. clinical study ID. */
    @Column(name = "reference_id")
    private String referenceId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public Long   getId()                              { return id; }
    public void   setId(Long id)                       { this.id = id; }

    public String getInvoiceNumber()                   { return invoiceNumber; }
    public void   setInvoiceNumber(String v)           { this.invoiceNumber = v; }

    public LocalDate getInvoiceDate()                  { return invoiceDate; }
    public void      setInvoiceDate(LocalDate v)       { this.invoiceDate = v; }

    public TaxInvoiceStatus getStatus()                { return status; }
    public void             setStatus(TaxInvoiceStatus v) { this.status = v; }

    public TaxType getTaxType()                        { return taxType; }
    public void    setTaxType(TaxType v)               { this.taxType = v; }

    public GstBillingProfile getBillingProfile()       { return billingProfile; }
    public void              setBillingProfile(GstBillingProfile v) { this.billingProfile = v; }

    public String getServiceDescription()              { return serviceDescription; }
    public void   setServiceDescription(String v)      { this.serviceDescription = v; }

    public String getSacCode()                         { return sacCode; }
    public void   setSacCode(String v)                 { this.sacCode = v; }

    public BigDecimal getBaseAmount()                  { return baseAmount; }
    public void       setBaseAmount(BigDecimal v)      { this.baseAmount = v; }

    public BigDecimal getCgstRate()                    { return cgstRate; }
    public void       setCgstRate(BigDecimal v)        { this.cgstRate = v; }

    public BigDecimal getCgstAmount()                  { return cgstAmount; }
    public void       setCgstAmount(BigDecimal v)      { this.cgstAmount = v; }

    public BigDecimal getSgstRate()                    { return sgstRate; }
    public void       setSgstRate(BigDecimal v)        { this.sgstRate = v; }

    public BigDecimal getSgstAmount()                  { return sgstAmount; }
    public void       setSgstAmount(BigDecimal v)      { this.sgstAmount = v; }

    public BigDecimal getIgstRate()                    { return igstRate; }
    public void       setIgstRate(BigDecimal v)        { this.igstRate = v; }

    public BigDecimal getIgstAmount()                  { return igstAmount; }
    public void       setIgstAmount(BigDecimal v)      { this.igstAmount = v; }

    public BigDecimal getTotalAmount()                 { return totalAmount; }
    public void       setTotalAmount(BigDecimal v)     { this.totalAmount = v; }

    public String getRazorpayOrderId()                 { return razorpayOrderId; }
    public void   setRazorpayOrderId(String v)         { this.razorpayOrderId = v; }

    public String getRazorpayPaymentId()               { return razorpayPaymentId; }
    public void   setRazorpayPaymentId(String v)       { this.razorpayPaymentId = v; }

    public String getPlaceOfSupply()                   { return placeOfSupply; }
    public void   setPlaceOfSupply(String v)           { this.placeOfSupply = v; }

    public Integer   getSubscriptionMonths()              { return subscriptionMonths; }
    public void      setSubscriptionMonths(Integer v)    { this.subscriptionMonths = v; }

    public LocalDate getSubscriptionStartDate()          { return subscriptionStartDate; }
    public void      setSubscriptionStartDate(LocalDate v) { this.subscriptionStartDate = v; }

    public LocalDate getSubscriptionEndDate()            { return subscriptionEndDate; }
    public void      setSubscriptionEndDate(LocalDate v) { this.subscriptionEndDate = v; }

    public String getReferenceId()                     { return referenceId; }
    public void   setReferenceId(String v)             { this.referenceId = v; }

    public LocalDateTime getCreatedAt()                { return createdAt; }
    public void          setCreatedAt(LocalDateTime v) { this.createdAt = v; }

    public LocalDateTime getPaidAt()                   { return paidAt; }
    public void          setPaidAt(LocalDateTime v)    { this.paidAt = v; }
}
