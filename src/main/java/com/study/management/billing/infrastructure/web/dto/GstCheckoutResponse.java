package com.study.management.billing.infrastructure.web.dto;

import com.study.management.billing.domain.model.TaxInvoice;
import com.study.management.billing.domain.model.TaxType;

import java.math.BigDecimal;

/**
 * Checkout response returned to the frontend after a DRAFT TaxInvoice is created.
 *
 * The frontend uses razorpayOrderId + amountInPaise to open the Razorpay checkout widget.
 * The tax breakdown is pre-computed so the doctor can confirm what they owe before paying.
 */
public class GstCheckoutResponse {

    private String     invoiceNumber;
    private String     razorpayOrderId;
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
    private String     invoiceDate;
    private String     status;
    private Integer    subscriptionMonths;
    private String     subscriptionStartDate;
    private String     subscriptionEndDate;

    public static GstCheckoutResponse from(TaxInvoice invoice) {
        GstCheckoutResponse r = new GstCheckoutResponse();
        r.invoiceNumber     = invoice.getInvoiceNumber();
        r.razorpayOrderId   = invoice.getRazorpayOrderId();
        r.taxType           = invoice.getTaxType().name();
        r.taxTypeLabel      = invoice.getTaxType() == TaxType.CGST_SGST
                              ? "CGST + SGST" : "IGST";
        r.baseAmount        = invoice.getBaseAmount();
        r.cgstRate          = invoice.getCgstRate();
        r.cgstAmount        = invoice.getCgstAmount();
        r.sgstRate          = invoice.getSgstRate();
        r.sgstAmount        = invoice.getSgstAmount();
        r.igstRate          = invoice.getIgstRate();
        r.igstAmount        = invoice.getIgstAmount();
        r.totalAmount       = invoice.getTotalAmount();
        r.amountInPaise     = invoice.getTotalAmount()
                              .multiply(BigDecimal.valueOf(100)).longValue();
        r.placeOfSupply     = invoice.getPlaceOfSupply();
        r.gstin             = invoice.getBillingProfile().getGstin();
        r.businessName      = invoice.getBillingProfile().getBusinessName();
        r.sacCode           = invoice.getSacCode();
        r.serviceDescription= invoice.getServiceDescription();
        r.invoiceDate           = invoice.getInvoiceDate().toString();
        r.status                = invoice.getStatus().name();
        r.subscriptionMonths    = invoice.getSubscriptionMonths();
        r.subscriptionStartDate = invoice.getSubscriptionStartDate() != null
                                  ? invoice.getSubscriptionStartDate().toString() : null;
        r.subscriptionEndDate   = invoice.getSubscriptionEndDate() != null
                                  ? invoice.getSubscriptionEndDate().toString() : null;
        return r;
    }

    public String     getInvoiceNumber()       { return invoiceNumber; }
    public String     getRazorpayOrderId()     { return razorpayOrderId; }
    public String     getTaxType()             { return taxType; }
    public String     getTaxTypeLabel()        { return taxTypeLabel; }
    public BigDecimal getBaseAmount()          { return baseAmount; }
    public BigDecimal getCgstRate()            { return cgstRate; }
    public BigDecimal getCgstAmount()          { return cgstAmount; }
    public BigDecimal getSgstRate()            { return sgstRate; }
    public BigDecimal getSgstAmount()          { return sgstAmount; }
    public BigDecimal getIgstRate()            { return igstRate; }
    public BigDecimal getIgstAmount()          { return igstAmount; }
    public BigDecimal getTotalAmount()         { return totalAmount; }
    public long       getAmountInPaise()       { return amountInPaise; }
    public String     getPlaceOfSupply()       { return placeOfSupply; }
    public String     getGstin()               { return gstin; }
    public String     getBusinessName()        { return businessName; }
    public String     getSacCode()             { return sacCode; }
    public String     getServiceDescription()  { return serviceDescription; }
    public String     getInvoiceDate()         { return invoiceDate; }
    public String     getStatus()              { return status; }
    public Integer    getSubscriptionMonths()  { return subscriptionMonths; }
    public String     getSubscriptionStartDate() { return subscriptionStartDate; }
    public String     getSubscriptionEndDate()   { return subscriptionEndDate; }
}
