package com.study.management.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Payment record — created when the user clicks "Proceed for Payment".
 * discountAmount is 0 unless a promo code was applied.
 * totalAmount = baseFee + gst - discountAmount.
 */
@Entity
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal baseFee;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal gst;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(length = 50)
    private String promoCodeUsed;

    private String paymentReference;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    @JsonBackReference("study-invoice")
    private Study study;

    // ── Getters & Setters ──────────────────────────────────────────────

    public Long getId()                             { return id; }
    public void setId(Long id)                      { this.id = id; }

    public BigDecimal getBaseFee()                  { return baseFee; }
    public void setBaseFee(BigDecimal baseFee)      { this.baseFee = baseFee; }

    public BigDecimal getGst()                      { return gst; }
    public void setGst(BigDecimal gst)              { this.gst = gst; }

    public BigDecimal getDiscountAmount()                       { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount)    { this.discountAmount = discountAmount; }

    public BigDecimal getTotalAmount()                  { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount)  { this.totalAmount = totalAmount; }

    public String getPromoCodeUsed()                        { return promoCodeUsed; }
    public void setPromoCodeUsed(String promoCodeUsed)      { this.promoCodeUsed = promoCodeUsed; }

    public String getPaymentReference()                         { return paymentReference; }
    public void setPaymentReference(String paymentReference)    { this.paymentReference = paymentReference; }

    public PaymentStatus getStatus()                { return status; }
    public void setStatus(PaymentStatus status)     { this.status = status; }

    public Study getStudy()                         { return study; }
    public void setStudy(Study study)               { this.study = study; }
}