package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.ClinicalPaymentStatus;

import java.util.UUID;

public class PaymentWebhookRequest {

    private UUID paymentId;
    private ClinicalPaymentStatus status;
    private String paymentGatewayRef;
    private String transactionId;

    public UUID getPaymentId()                          { return paymentId; }
    public void setPaymentId(UUID v)                   { this.paymentId = v; }
    public ClinicalPaymentStatus getStatus()            { return status; }
    public void setStatus(ClinicalPaymentStatus v)     { this.status = v; }
    public String getPaymentGatewayRef()                { return paymentGatewayRef; }
    public void setPaymentGatewayRef(String v)         { this.paymentGatewayRef = v; }
    public String getTransactionId()                    { return transactionId; }
    public void setTransactionId(String v)             { this.transactionId = v; }
}
