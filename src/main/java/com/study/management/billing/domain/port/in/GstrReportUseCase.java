package com.study.management.billing.domain.port.in;

public interface GstrReportUseCase {

    /**
     * Generates a GSTR-1 compliant CSV for all PAID invoices in the given month.
     * Segregated by Place of Supply (state code) for easy filing.
     *
     * @param year  e.g. 2026
     * @param month 1-12
     * @return CSV string ready for download or filing upload
     */
    String generateGstr1Csv(int year, int month);
}
