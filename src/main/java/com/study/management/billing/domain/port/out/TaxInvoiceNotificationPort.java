package com.study.management.billing.domain.port.out;

import com.study.management.billing.domain.model.TaxInvoice;

public interface TaxInvoiceNotificationPort {

    /**
     * Dispatches the finalized Tax Invoice (PDF or structured data) to the doctor's
     * registered business email.  Called automatically on payment confirmation.
     */
    void dispatchInvoice(TaxInvoice invoice);
}
