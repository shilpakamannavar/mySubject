package com.study.management.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Links a promo code to a specific customer, or to ALL customers when customer is null.
 */
@Entity
@Table(name = "promo_code_assignment",
       uniqueConstraints = @UniqueConstraint(name = "uq_assignment", columnNames = {"promo_code_id", "customer_id"}))
public class PromoCodeAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "promo_code_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private PromoCode promoCode;

    // null = assigned to ALL customers
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Customer customer;

    private LocalDateTime assignedAt;

    @PrePersist
    private void onCreate() {
        if (assignedAt == null) assignedAt = LocalDateTime.now();
    }

    // ── Getters & Setters ──────────────────────────────────────────────

    public Long getId()                                  { return id; }
    public void setId(Long id)                           { this.id = id; }

    public PromoCode getPromoCode()                      { return promoCode; }
    public void setPromoCode(PromoCode promoCode)        { this.promoCode = promoCode; }

    public Customer getCustomer()                        { return customer; }
    public void setCustomer(Customer customer)           { this.customer = customer; }

    public LocalDateTime getAssignedAt()                 { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt)  { this.assignedAt = assignedAt; }

    public String getScope() {
        return customer == null ? "ALL" : "SPECIFIC";
    }
}
