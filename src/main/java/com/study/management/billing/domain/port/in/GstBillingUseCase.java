package com.study.management.billing.domain.port.in;

import com.study.management.billing.domain.model.TaxInvoice;

import java.math.BigDecimal;

public interface GstBillingUseCase {

    record GstinValidateResult(boolean valid, String stateCode, String stateName, String errorMessage) {}

    record CheckoutCommand(
            String     phoneNumber,        // E.164 phone of the PI or Hospital/SMO — resolved server-side
            String     businessName,
            String     gstin,
            String     billingState,
            String     registeredAddress,
            String     businessEmail,
            BigDecimal baseAmount,
            String     serviceDescription,
            String     sacCode,
            String     referenceId,
            int        subscriptionMonths  // 1–36; determines coverage period and enrollment gate
    ) {}

    /**
     * Validates GSTIN format, state code, and declared billing-state consistency.
     * Blocked if first-2-digit state code does not match the declared state.
     */
    GstinValidateResult validateGstin(String gstin, String declaredBillingState);

    /**
     * Full checkout pipeline:
     *   validate GSTIN → upsert GstBillingProfile → compute CGST/SGST or IGST
     *   → allocate sequential invoice number → create Razorpay order with GSTIN in notes
     *   → return DRAFT TaxInvoice.
     */
    TaxInvoice initiateCheckout(CheckoutCommand command);

    /** Called by the Razorpay webhook on order.paid — marks invoice PAID and dispatches it. */
    TaxInvoice confirmPayment(String razorpayOrderId, String razorpayPaymentId);

    /** Called by the Razorpay webhook on payment.failed. */
    TaxInvoice failPayment(String razorpayOrderId);

    TaxInvoice getInvoice(String invoiceNumber);

    TaxInvoice getInvoiceByOrderId(String razorpayOrderId);
}
