package com.study.management.billing.infrastructure.web.dto;

import com.study.management.billing.domain.model.GstBillingProfile;
import com.study.management.billing.domain.model.TaxInvoice;
import com.study.management.billing.domain.model.TaxType;
import com.study.management.billing.domain.service.GstTaxEngine;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Full invoice response for GET /api/billing/invoice/{number} and by-order/{id}.
 * Includes supplier identity and the billing profile so the frontend can render
 * a GST-compliant tax invoice receipt.
 */
public class TaxInvoiceResponse {

    // ── Invoice header ─────────────────────────────────────────────────────────
    private String invoiceNumber;
    private String invoiceDate;
    private String paidAt;
    private String status;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String referenceId;

    // ── Tax breakdown ──────────────────────────────────────────────────────────
    private String     taxType;
    private String     taxTypeLabel;
    private BigDecimal baseAmount;
    private BigDecimal cgstRate;
    private BigDecimal cgstAmount;
    private BigDecimal sgstRate;
    private BigDecimal sgstAmount;
    private BigDecimal igstRate;
    private BigDecimal igstAmount;
    private BigDecimal totalAmount;
    private long       amountInPaise;
    private String     placeOfSupply;
    private String     gstin;
    private String     businessName;
    private String     sacCode;
    private String     serviceDescription;

    // ── Supplier (platform) fields ─────────────────────────────────────────────
    private String supplierName;
    private String supplierGstin;
    private String supplierAddress;
    private String supplierState;
    private String supplierEmail;

    // ── Billing profile (customer) ─────────────────────────────────────────────
    private BillingProfileDto billingProfile;

    public static class BillingProfileDto {
        private String  businessName;
        private String  registeredAddress;
        private String  businessEmail;
        private String  billingState;
        private boolean itcEligible;

        static BillingProfileDto from(GstBillingProfile p) {
            BillingProfileDto d = new BillingProfileDto();
            d.businessName      = p.getBusinessName();
            d.registeredAddress = p.getRegisteredAddress();
            d.businessEmail     = p.getBusinessEmail();
            d.billingState      = p.getBillingState();
            d.itcEligible       = p.isItcEligible();
            return d;
        }

        public String  getBusinessName()      { return businessName; }
        public String  getRegisteredAddress() { return registeredAddress; }
        public String  getBusinessEmail()     { return businessEmail; }
        public String  getBillingState()      { return billingState; }
        public boolean isItcEligible()        { return itcEligible; }
    }

    // ── Factory ────────────────────────────────────────────────────────────────

    public static TaxInvoiceResponse from(TaxInvoice invoice,
                                          String supplierName,
                                          String supplierGstin,
                                          String supplierAddress,
                                          String supplierState,
                                          String supplierEmail) {
        TaxInvoiceResponse r = new TaxInvoiceResponse();
        r.invoiceNumber      = invoice.getInvoiceNumber();
        r.invoiceDate        = invoice.getInvoiceDate().toString();
        r.paidAt             = invoice.getPaidAt() != null ? invoice.getPaidAt().toString() : null;
        r.status             = invoice.getStatus().name();
        r.razorpayOrderId    = invoice.getRazorpayOrderId();
        r.razorpayPaymentId  = invoice.getRazorpayPaymentId();
        r.referenceId        = invoice.getReferenceId();

        r.taxType            = invoice.getTaxType().name();
        r.taxTypeLabel       = invoice.getTaxType() == TaxType.CGST_SGST
                               ? "CGST + SGST (Intra-State)" : "IGST (Inter-State)";
        r.baseAmount         = invoice.getBaseAmount();
        r.cgstRate           = invoice.getCgstRate();
        r.cgstAmount         = invoice.getCgstAmount();
        r.sgstRate           = invoice.getSgstRate();
        r.sgstAmount         = invoice.getSgstAmount();
        r.igstRate           = invoice.getIgstRate();
        r.igstAmount         = invoice.getIgstAmount();
        r.totalAmount        = invoice.getTotalAmount();
        r.amountInPaise      = invoice.getTotalAmount()
                               .multiply(BigDecimal.valueOf(100)).longValue();
        String posCode       = invoice.getPlaceOfSupply();
        String posName       = GstTaxEngine.STATE_CODES.getOrDefault(posCode, posCode);
        r.placeOfSupply      = toTitleCase(posName) + " (Code: " + posCode + ")";
        r.gstin              = invoice.getBillingProfile().getGstin();
        r.businessName       = invoice.getBillingProfile().getBusinessName();
        r.sacCode            = invoice.getSacCode();
        r.serviceDescription = invoice.getServiceDescription();

        r.supplierName       = supplierName;
        r.supplierGstin      = supplierGstin;
        r.supplierAddress    = supplierAddress;
        r.supplierState      = supplierState;
        r.supplierEmail      = supplierEmail;

        r.billingProfile     = BillingProfileDto.from(invoice.getBillingProfile());
        return r;
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public String     getInvoiceNumber()      { return invoiceNumber; }
    public String     getInvoiceDate()        { return invoiceDate; }
    public String     getPaidAt()             { return paidAt; }
    public String     getStatus()             { return status; }
    public String     getRazorpayOrderId()    { return razorpayOrderId; }
    public String     getRazorpayPaymentId()  { return razorpayPaymentId; }
    public String     getReferenceId()        { return referenceId; }
    public String     getTaxType()            { return taxType; }
    public String     getTaxTypeLabel()       { return taxTypeLabel; }
    public BigDecimal getBaseAmount()         { return baseAmount; }
    public BigDecimal getCgstRate()           { return cgstRate; }
    public BigDecimal getCgstAmount()         { return cgstAmount; }
    public BigDecimal getSgstRate()           { return sgstRate; }
    public BigDecimal getSgstAmount()         { return sgstAmount; }
    public BigDecimal getIgstRate()           { return igstRate; }
    public BigDecimal getIgstAmount()         { return igstAmount; }
    public BigDecimal getTotalAmount()        { return totalAmount; }
    public long       getAmountInPaise()      { return amountInPaise; }
    public String     getPlaceOfSupply()      { return placeOfSupply; }
    public String     getGstin()              { return gstin; }
    public String     getBusinessName()       { return businessName; }
    public String     getSacCode()            { return sacCode; }
    public String     getServiceDescription() { return serviceDescription; }
    public String     getSupplierName()       { return supplierName; }
    public String     getSupplierGstin()      { return supplierGstin; }
    public String     getSupplierAddress()    { return supplierAddress; }
    public String     getSupplierState()      { return supplierState; }
    public String     getSupplierEmail()      { return supplierEmail; }
    public BillingProfileDto getBillingProfile() { return billingProfile; }

    private static String toTitleCase(String upper) {
        if (upper == null || upper.isBlank()) return upper;
        StringBuilder sb = new StringBuilder();
        for (String word : upper.split(" ")) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1).toLowerCase())
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }
}
