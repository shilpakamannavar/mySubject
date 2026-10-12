package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Invoice details returned after fee calculation and payment initiation")
public class InvoiceResponse {

    @Schema(description = "DB-generated invoice ID (null if not yet persisted)")
    private Long invoiceId;

    @Schema(description = "Human-readable study identifier", example = "STUDY-2025-001")
    private String studyId;

    @Schema(description = "Base fee = patients × visits × ₹500", example = "10000.00")
    private BigDecimal baseFee;

    @Schema(description = "GST at 18% of base fee", example = "1800.00")
    private BigDecimal gst;

    @Schema(description = "Discount applied by promo code (₹0 if no promo)", example = "0.00")
    private BigDecimal discountAmount;

    @Schema(description = "Final amount charged = baseFee + gst − discount", example = "11800.00")
    private BigDecimal totalAmount;

    @Schema(description = "Promo code that was applied, or null", example = "SAVE20")
    private String promoCodeUsed;

    @Schema(description = "True when a promo code was applied", example = "false")
    private boolean promoApplied;

    @Schema(description = "UPI transaction reference or PROMO_WAIVED_XXX", example = "UPI_TXN_A3B4C5D6E7F8")
    private String paymentReference;

    @Schema(description = "Payment outcome", example = "SUCCESS")
    private PaymentStatus paymentStatus;

    // ── Razorpay checkout fields (populated only when gateway = Razorpay) ──

    @Schema(description = "Razorpay order ID — pass to the Razorpay checkout widget", example = "order_OmXHJuEj3FdBLi")
    private String razorpayOrderId;

    @Schema(description = "Razorpay public key (key.id) — safe to use in frontend", example = "rzp_test_XXXXXXXXXXXXXXXX")
    private String razorpayKeyId;

    @Schema(description = "Currency for Razorpay checkout", example = "INR")
    private String razorpayCurrency;

    @Schema(description = "True when the frontend must render the Razorpay checkout to complete payment")
    private boolean requiresCheckout;

    // Getters & Setters

    public Long getInvoiceId()                              { return invoiceId; }
    public void setInvoiceId(Long invoiceId)                { this.invoiceId = invoiceId; }

    public String getStudyId()                              { return studyId; }
    public void setStudyId(String studyId)                  { this.studyId = studyId; }

    public BigDecimal getBaseFee()                          { return baseFee; }
    public void setBaseFee(BigDecimal baseFee)              { this.baseFee = baseFee; }

    public BigDecimal getGst()                              { return gst; }
    public void setGst(BigDecimal gst)                      { this.gst = gst; }

    public BigDecimal getDiscountAmount()                   { return discountAmount; }
    public void setDiscountAmount(BigDecimal d)             { this.discountAmount = d; }

    public BigDecimal getTotalAmount()                      { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount)      { this.totalAmount = totalAmount; }

    public String getPromoCodeUsed()                        { return promoCodeUsed; }
    public void setPromoCodeUsed(String promoCodeUsed)      { this.promoCodeUsed = promoCodeUsed; }

    public boolean isPromoApplied()                         { return promoApplied; }
    public void setPromoApplied(boolean promoApplied)       { this.promoApplied = promoApplied; }

    public String getPaymentReference()                     { return paymentReference; }
    public void setPaymentReference(String paymentReference){ this.paymentReference = paymentReference; }

    public PaymentStatus getPaymentStatus()                 { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus){ this.paymentStatus = paymentStatus; }

    public String getRazorpayOrderId()                      { return razorpayOrderId; }
    public void setRazorpayOrderId(String id)               { this.razorpayOrderId = id; }

    public String getRazorpayKeyId()                        { return razorpayKeyId; }
    public void setRazorpayKeyId(String keyId)              { this.razorpayKeyId = keyId; }

    public String getRazorpayCurrency()                     { return razorpayCurrency; }
    public void setRazorpayCurrency(String currency)        { this.razorpayCurrency = currency; }

    public boolean isRequiresCheckout()                     { return requiresCheckout; }
    public void setRequiresCheckout(boolean v)              { this.requiresCheckout = v; }
}