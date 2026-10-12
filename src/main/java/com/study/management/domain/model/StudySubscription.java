package com.study.management.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * One record per PI per calendar month.
 * A PAID record unlocks subject enrollment for ALL studies owned by that PI.
 */
@Entity
@Table(name = "study_subscriptions",
       uniqueConstraints = @UniqueConstraint(columnNames = {"pi_id", "billing_year", "billing_month"}))
public class StudySubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "subscription_id")
    private UUID subscriptionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pi_id", nullable = false)
    private PrimaryInvestigator primaryInvestigator;

    @Column(name = "billing_year", nullable = false)
    private Integer billingYear;

    @Column(name = "billing_month", nullable = false)
    private Integer billingMonth;

    @Column(name = "duration_months", nullable = false)
    private Integer durationMonths = 1;

    @Column(name = "subscription_start_date")
    private LocalDate subscriptionStartDate;

    @Column(name = "subscription_end_date")
    private LocalDate subscriptionEndDate;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status = SubscriptionStatus.PENDING;

    @Column(name = "paid_at")
    private OffsetDateTime paidAt;

    @Column(name = "paid_by")
    private String paidBy;

    @Column(name = "payment_reference")
    private String paymentReference;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }

    public UUID getSubscriptionId()                            { return subscriptionId; }
    public void setSubscriptionId(UUID v)                     { this.subscriptionId = v; }

    public PrimaryInvestigator getPrimaryInvestigator()        { return primaryInvestigator; }
    public void setPrimaryInvestigator(PrimaryInvestigator v) { this.primaryInvestigator = v; }

    public Integer getBillingYear()                            { return billingYear; }
    public void setBillingYear(Integer v)                     { this.billingYear = v; }

    public Integer getBillingMonth()                           { return billingMonth; }
    public void setBillingMonth(Integer v)                    { this.billingMonth = v; }

    public Integer getDurationMonths()                         { return durationMonths; }
    public void setDurationMonths(Integer v)                  { this.durationMonths = v; }

    public LocalDate getSubscriptionStartDate()               { return subscriptionStartDate; }
    public void setSubscriptionStartDate(LocalDate v)         { this.subscriptionStartDate = v; }

    public LocalDate getSubscriptionEndDate()                 { return subscriptionEndDate; }
    public void setSubscriptionEndDate(LocalDate v)           { this.subscriptionEndDate = v; }

    public BigDecimal getAmount()                              { return amount; }
    public void setAmount(BigDecimal v)                       { this.amount = v; }

    public SubscriptionStatus getStatus()                      { return status; }
    public void setStatus(SubscriptionStatus v)               { this.status = v; }

    public OffsetDateTime getPaidAt()                          { return paidAt; }
    public void setPaidAt(OffsetDateTime v)                   { this.paidAt = v; }

    public String getPaidBy()                                  { return paidBy; }
    public void setPaidBy(String v)                           { this.paidBy = v; }

    public String getPaymentReference()                        { return paymentReference; }
    public void setPaymentReference(String v)                 { this.paymentReference = v; }

    public OffsetDateTime getCreatedAt()                       { return createdAt; }
    public void setCreatedAt(OffsetDateTime v)                { this.createdAt = v; }
}
