package com.study.management.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Promo code entity.
 * FULL → totalAmount becomes ₹0, no gateway call.
 * PERCENTAGE → discount = gross × (value / 100).
 * FLAT → discount = min(value, gross).
 */
@Entity
@Table(name = "promo_code")
public class PromoCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Column(precision = 10, scale = 2)
    private BigDecimal discountValue; // null for FULL

    private LocalDate expiryDate;     // null = no expiry

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    private Integer maxUsages;        // null = unlimited

    @Column(nullable = false)
    private Integer usedCount = 0;

    // ── Getters & Setters ──────────────────────────────────────────────

    public Long getId()                             { return id; }
    public void setId(Long id)                      { this.id = id; }

    public String getCode()                         { return code; }
    public void setCode(String code)                { this.code = code; }

    public DiscountType getDiscountType()                       { return discountType; }
    public void setDiscountType(DiscountType discountType)      { this.discountType = discountType; }

    public BigDecimal getDiscountValue()                        { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue)      { this.discountValue = discountValue; }

    public LocalDate getExpiryDate()                { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public boolean isActive()                       { return active; }
    public void setActive(boolean active)           { this.active = active; }

    public Integer getMaxUsages()                   { return maxUsages; }
    public void setMaxUsages(Integer maxUsages)     { this.maxUsages = maxUsages; }

    public Integer getUsedCount()                   { return usedCount; }
    public void setUsedCount(Integer usedCount)     { this.usedCount = usedCount; }
}