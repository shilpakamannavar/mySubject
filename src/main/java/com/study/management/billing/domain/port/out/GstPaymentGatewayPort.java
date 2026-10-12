package com.study.management.billing.domain.port.out;

import java.math.BigDecimal;

public interface GstPaymentGatewayPort {

    record GstOrderResult(String orderId, String currency, long amountInPaise) {}

    /**
     * Creates a payment order with the doctor's GSTIN and business name embedded in the
     * Razorpay notes object, satisfying the B2B audit trail requirement.
     *
     * @param totalAmountInRupees the GST-inclusive total
     * @param gstin               validated 15-char GSTIN to store in order notes
     * @param businessName        legal trade name to store in order notes
     * @param taxInvoiceNumber    sequential invoice number for cross-reference
     */
    GstOrderResult createOrder(BigDecimal totalAmountInRupees, String gstin,
                               String businessName, String taxInvoiceNumber);
}
