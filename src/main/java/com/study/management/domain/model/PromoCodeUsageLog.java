package com.study.management.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Immutable audit record written once per promo redemption.
 * Snapshots customer name and study ID so the log survives entity deletes.
 */
@Entity
@Table(name = "promo_code_usage_log")
public class PromoCodeUsageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String promoCode;

    // FK; nullable because pre-customer studies have no customer association
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private String customerName;        // snapshot at redemption time

    @Column(nullable = false)
    private String studyBusinessId;     // e.g. STUDY-2026-NEW

    @Column(nullable = false)
    private Long studyDbId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(nullable = false)
    private LocalDateTime usedAt;

    @PrePersist
    private void onCreate() {
        if (usedAt == null) usedAt = LocalDateTime.now();
    }

    // ── Getters & Setters ──────────────────────────────────────────────

    public Long getId()                                { return id; }
    public void setId(Long id)                         { this.id = id; }

    public String getPromoCode()                       { return promoCode; }
    public void setPromoCode(String promoCode)         { this.promoCode = promoCode; }

    public Customer getCustomer()                      { return customer; }
    public void setCustomer(Customer customer)         { this.customer = customer; }

    public String getCustomerName()                    { return customerName; }
    public void setCustomerName(String customerName)   { this.customerName = customerName; }

    public String getStudyBusinessId()                 { return studyBusinessId; }
    public void setStudyBusinessId(String id)          { this.studyBusinessId = id; }

    public Long getStudyDbId()                         { return studyDbId; }
    public void setStudyDbId(Long studyDbId)           { this.studyDbId = studyDbId; }

    public BigDecimal getDiscountAmount()                         { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount)      { this.discountAmount = discountAmount; }

    public LocalDateTime getUsedAt()                   { return usedAt; }
    public void setUsedAt(LocalDateTime usedAt)        { this.usedAt = usedAt; }
}
