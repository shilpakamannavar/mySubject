package com.study.management.infrastructure.web.dto;

/**
 * Payload sent by the Razorpay frontend checkout to our server
 * after the user completes (or fails) a payment.
 *
 * On success the frontend receives all three fields from Razorpay.
 * On failure only razorpayOrderId is present.
 */
public class RazorpayCallbackRequest {

    private String razorpayOrderId;    // order_XXXXXXXXXXXXXXXX (always present)
    private String razorpayPaymentId;  // pay_XXXXXXXXXXXXXXXX  (present on success)
    private String razorpaySignature;  // HMAC-SHA256 signature  (present on success)

    public String getRazorpayOrderId()                      { return razorpayOrderId; }
    public void   setRazorpayOrderId(String id)             { this.razorpayOrderId = id; }

    public String getRazorpayPaymentId()                    { return razorpayPaymentId; }
    public void   setRazorpayPaymentId(String id)           { this.razorpayPaymentId = id; }

    public String getRazorpaySignature()                    { return razorpaySignature; }
    public void   setRazorpaySignature(String sig)          { this.razorpaySignature = sig; }

    public boolean isSuccess() {
        return razorpayPaymentId != null && !razorpayPaymentId.isBlank()
            && razorpaySignature  != null && !razorpaySignature.isBlank();
    }
}