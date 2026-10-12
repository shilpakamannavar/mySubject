package com.study.management.billing.application;

import com.study.management.billing.domain.model.TaxInvoice;
import com.study.management.billing.domain.port.in.GstrReportUseCase;
import com.study.management.billing.domain.port.out.TaxInvoicePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * Generates a GSTR-1 compliant CSV for all PAID B2B invoices in a given month.
 * Entries are sorted by Place of Supply (state code) to match the state-wise
 * segregation required for GST filing.
 */
@Service
@Transactional(readOnly = true)
public class GstrReportApplicationService implements GstrReportUseCase {

    private final TaxInvoicePort invoicePort;

    public GstrReportApplicationService(TaxInvoicePort invoicePort) {
        this.invoicePort = invoicePort;
    }

    @Override
    public String generateGstr1Csv(int year, int month) {
        List<TaxInvoice> invoices = invoicePort.findPaidByYearMonth(year, month)
                .stream()
                .sorted(Comparator.comparing(TaxInvoice::getPlaceOfSupply)
                        .thenComparing(TaxInvoice::getInvoiceNumber))
                .toList();

        StringBuilder csv = new StringBuilder();
        csv.append("GSTIN of Recipient,Receiver Name,Invoice Number,Invoice Date,Invoice Value (₹),")
           .append("Place of Supply,Taxable Value (₹),Tax Type,")
           .append("CGST Rate (%),CGST Amount (₹),SGST Rate (%),SGST Amount (₹),")
           .append("IGST Rate (%),IGST Amount (₹),SAC Code,ITC Eligible\n");

        for (TaxInvoice inv : invoices) {
            csv.append(escape(inv.getBillingProfile().getGstin())).append(",")
               .append(escape(inv.getBillingProfile().getBusinessName())).append(",")
               .append(escape(inv.getInvoiceNumber())).append(",")
               .append(inv.getInvoiceDate()).append(",")
               .append(inv.getTotalAmount()).append(",")
               .append(inv.getPlaceOfSupply()).append(",")
               .append(inv.getBaseAmount()).append(",")
               .append(inv.getTaxType()).append(",")
               .append(inv.getCgstRate()).append(",")
               .append(inv.getCgstAmount()).append(",")
               .append(inv.getSgstRate()).append(",")
               .append(inv.getSgstAmount()).append(",")
               .append(inv.getIgstRate()).append(",")
               .append(inv.getIgstAmount()).append(",")
               .append(escape(inv.getSacCode())).append(",")
               .append(inv.getBillingProfile().isItcEligible() ? "Y" : "N")
               .append("\n");
        }
        return csv.toString();
    }

    /** Wraps fields that may contain commas in double-quotes. */
    private static String escape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n"))
            return "\"" + value.replace("\"", "\"\"") + "\"";
        return value;
    }
}
