package com.study.management.billing.domain.model;

import java.math.BigDecimal;

public record TaxBreakdown(
        TaxType    taxType,
        BigDecimal cgstRate,
        BigDecimal cgstAmount,
        BigDecimal sgstRate,
        BigDecimal sgstAmount,
        BigDecimal igstRate,
        BigDecimal igstAmount,
        BigDecimal totalAmount
) {}
