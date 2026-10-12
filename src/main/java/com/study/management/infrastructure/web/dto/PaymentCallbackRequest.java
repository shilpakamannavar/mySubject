package com.study.management.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload sent by the UPI gateway in the payment callback webhook")
public class PaymentCallbackRequest {

    @Schema(description = "Transaction reference returned by the payment provider", example = "UPI_TXN_A3B4C5D6E7F8")
    private String paymentReference;

    @Schema(description = "true = payment succeeded, false = payment failed", example = "true")
    private boolean success;

    public String getPaymentReference()                         { return paymentReference; }
    public void setPaymentReference(String paymentReference)    { this.paymentReference = paymentReference; }

    public boolean isSuccess()                                  { return success; }
    public void setSuccess(boolean success)                     { this.success = success; }
}