package com.study.management.billing.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "gst_billing_profile")
public class GstBillingProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "gstin", length = 15, nullable = false, unique = true)
    private String gstin;

    /** 2-digit numeric state code extracted from the first two characters of the GSTIN. */
    @Column(name = "place_of_supply_code", length = 2, nullable = false)
    private String placeOfSupplyCode;

    @Column(name = "billing_state")
    private String billingState;

    @Column(name = "registered_address")
    private String registeredAddress;

    @Column(name = "business_email")
    private String businessEmail;

    /**
     * FK back to the Primary Investigator who owns this billing profile.
     * Exactly one of piId / hospitalId must be set.
     */
    @Column(name = "pi_id")
    private UUID piId;

    /**
     * FK back to the Hospital / SMO that owns this billing profile.
     * Exactly one of piId / hospitalId must be set.
     */
    @Column(name = "hospital_id")
    private UUID hospitalId;

    /** True once GSTIN is successfully verified against the GST registry. */
    @Column(name = "itc_eligible")
    private boolean itcEligible = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long   getId()                                      { return id; }
    public void   setId(Long id)                               { this.id = id; }

    public String getBusinessName()                            { return businessName; }
    public void   setBusinessName(String businessName)         { this.businessName = businessName; }

    public String getGstin()                                   { return gstin; }
    public void   setGstin(String gstin)                       { this.gstin = gstin; }

    public String getPlaceOfSupplyCode()                       { return placeOfSupplyCode; }
    public void   setPlaceOfSupplyCode(String code)            { this.placeOfSupplyCode = code; }

    public String getBillingState()                            { return billingState; }
    public void   setBillingState(String billingState)         { this.billingState = billingState; }

    public String getRegisteredAddress()                       { return registeredAddress; }
    public void   setRegisteredAddress(String registeredAddress){ this.registeredAddress = registeredAddress; }

    public String getBusinessEmail()                           { return businessEmail; }
    public void   setBusinessEmail(String businessEmail)       { this.businessEmail = businessEmail; }

    public UUID getPiId()                                      { return piId; }
    public void setPiId(UUID piId)                             { this.piId = piId; }

    public UUID getHospitalId()                                { return hospitalId; }
    public void setHospitalId(UUID hospitalId)                 { this.hospitalId = hospitalId; }

    public boolean isItcEligible()                             { return itcEligible; }
    public void    setItcEligible(boolean itcEligible)         { this.itcEligible = itcEligible; }

    public LocalDateTime getCreatedAt()                        { return createdAt; }
    public void          setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
