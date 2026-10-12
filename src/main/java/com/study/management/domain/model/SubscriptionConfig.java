package com.study.management.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "subscription_config")
public class SubscriptionConfig {

    @Id
    private Long id = 1L;

    @Column(name = "monthly_rate", precision = 10, scale = 2, nullable = false)
    private BigDecimal monthlyRate = new BigDecimal("299.00");

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @PrePersist
    @PreUpdate
    private void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public Long getId()                              { return id; }
    public void setId(Long id)                       { this.id = id; }

    public BigDecimal getMonthlyRate()               { return monthlyRate; }
    public void setMonthlyRate(BigDecimal rate)      { this.monthlyRate = rate; }

    public OffsetDateTime getUpdatedAt()             { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime v)       { this.updatedAt = v; }

    public String getUpdatedBy()                     { return updatedBy; }
    public void setUpdatedBy(String v)               { this.updatedBy = v; }
}
