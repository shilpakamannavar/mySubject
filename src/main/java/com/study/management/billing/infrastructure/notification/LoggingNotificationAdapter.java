package com.study.management.billing.infrastructure.notification;

import com.study.management.billing.domain.model.TaxInvoice;
import com.study.management.billing.domain.port.out.TaxInvoiceNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Notification adapter — logs invoice details to console/file.
 *
 * Production swap: replace with an adapter that generates a PDF (iText / Apache PDFBox)
 * and sends it via SES / SendGrid to billingProfile.businessEmail.
 */
@Component
public class LoggingNotificationAdapter implements TaxInvoiceNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingNotificationAdapter.class);

    @Override
    public void dispatchInvoice(TaxInvoice invoice) {
        log.info("""
                ═══════════════════════════════════════════════════════════
                                      TAX INVOICE
                ═══════════════════════════════════════════════════════════
                Invoice No  : {}
                Date        : {}
                Status      : {}
                ───────────────────────────────────────────────────────────
                Recipient   : {}
                GSTIN       : {}
                State (POS) : {}
                ───────────────────────────────────────────────────────────
                Service     : {}
                SAC Code    : {}
                ───────────────────────────────────────────────────────────
                Base Amount : ₹{}
                {}
                Total       : ₹{}
                ───────────────────────────────────────────────────────────
                Payment Ref : {}
                ═══════════════════════════════════════════════════════════
                → Dispatch email to: {}
                ═══════════════════════════════════════════════════════════
                """,
                invoice.getInvoiceNumber(),
                invoice.getInvoiceDate(),
                invoice.getStatus(),
                invoice.getBillingProfile().getBusinessName(),
                invoice.getBillingProfile().getGstin(),
                invoice.getPlaceOfSupply(),
                invoice.getServiceDescription(),
                invoice.getSacCode(),
                invoice.getBaseAmount(),
                taxLine(invoice),
                invoice.getTotalAmount(),
                invoice.getRazorpayPaymentId(),
                invoice.getBillingProfile().getBusinessEmail());
    }

    private static String taxLine(TaxInvoice inv) {
        return switch (inv.getTaxType()) {
            case CGST_SGST -> String.format("CGST %s%%: ₹%s  |  SGST %s%%: ₹%s",
                    inv.getCgstRate(), inv.getCgstAmount(),
                    inv.getSgstRate(), inv.getSgstAmount());
            case IGST      -> String.format("IGST  %s%%: ₹%s",
                    inv.getIgstRate(), inv.getIgstAmount());
        };
    }
}
