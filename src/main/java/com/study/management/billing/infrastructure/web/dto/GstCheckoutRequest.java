package com.study.management.billing.infrastructure.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class GstCheckoutRequest {

    /**
     * E.164 phone number of the billing entity (PI or Hospital/SMO).
     * This is the phone number used at sign-up — taken directly from AuthContext on the frontend.
     * The backend resolves the PI or Hospital record by phone lookup.
     */
    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^\\+[1-9]\\d{6,14}$",
        message = "Phone number must be in E.164 format (e.g. +919972936720)"
    )
    private String phoneNumber;

    @NotBlank(message = "Business/Clinic legal name is required")
    private String businessName;

    @NotBlank(message = "GSTIN is required")
    @Size(min = 15, max = 15, message = "GSTIN must be exactly 15 characters")
    private String gstin;

    @NotBlank(message = "Billing state is required")
    private String billingState;

    @NotBlank(message = "Registered business address is required")
    private String registeredAddress;

    @Email(message = "Must be a valid email address")
    private String businessEmail;

    @NotNull(message = "Base amount is required")
    @DecimalMin(value = "1.00", message = "Base amount must be at least ₹1.00")
    private BigDecimal baseAmount;

    /** Optional — defaults to platform service description when blank. */
    private String serviceDescription;

    /** Optional — defaults to 997331 (medical software licensing) when blank. */
    private String sacCode;

    /**
     * Number of months this payment covers (1–36).
     * Determines the subscription window: startDate to startDate + N months - 1 day.
     * Subject enrollment is blocked for any month not covered by a PAID invoice.
     */
    @NotNull(message = "subscriptionMonths is required")
    @Min(value = 1, message = "subscriptionMonths must be at least 1")
    @Max(value = 36, message = "subscriptionMonths cannot exceed 36")
    private Integer subscriptionMonths;

    /** Optional back-reference: e.g. clinicalStudyId for traceability. */
    private String referenceId;

    public String     getBusinessName()                          { return businessName; }
    public void       setBusinessName(String businessName)       { this.businessName = businessName; }

    public String     getGstin()                                 { return gstin; }
    public void       setGstin(String gstin)                     { this.gstin = gstin; }

    public String     getBillingState()                          { return billingState; }
    public void       setBillingState(String billingState)       { this.billingState = billingState; }

    public String     getRegisteredAddress()                     { return registeredAddress; }
    public void       setRegisteredAddress(String v)             { this.registeredAddress = v; }

    public String     getBusinessEmail()                         { return businessEmail; }
    public void       setBusinessEmail(String businessEmail)     { this.businessEmail = businessEmail; }

    public BigDecimal getBaseAmount()                            { return baseAmount; }
    public void       setBaseAmount(BigDecimal baseAmount)       { this.baseAmount = baseAmount; }

    public String     getServiceDescription()                    { return serviceDescription; }
    public void       setServiceDescription(String v)            { this.serviceDescription = v; }

    public String     getSacCode()                               { return sacCode; }
    public void       setSacCode(String sacCode)                 { this.sacCode = sacCode; }

    public Integer    getSubscriptionMonths()                     { return subscriptionMonths; }
    public void       setSubscriptionMonths(Integer v)            { this.subscriptionMonths = v; }

    public String     getReferenceId()                           { return referenceId; }
    public void       setReferenceId(String referenceId)         { this.referenceId = referenceId; }

    public String     getPhoneNumber()                           { return phoneNumber; }
    public void       setPhoneNumber(String phoneNumber)         { this.phoneNumber = phoneNumber; }
}
