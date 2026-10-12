package com.study.management.billing.domain.service;

import com.study.management.billing.domain.model.TaxBreakdown;
import com.study.management.billing.domain.model.TaxType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * Pure domain service — no I/O, no Spring transactionality.
 * Owns the GSTIN format check, state-code extraction, and the B2B tax matrix.
 *
 * Intra-state (doctor GSTIN state == platform state): CGST 9% + SGST 9%
 * Inter-state (states differ):                        IGST 18%
 */
@Component
public class GstTaxEngine {

    private static final BigDecimal CGST_RATE = new BigDecimal("9.00");
    private static final BigDecimal SGST_RATE = new BigDecimal("9.00");
    private static final BigDecimal IGST_RATE = new BigDecimal("18.00");

    /** GST-council prescribed state / UT codes mapped to their canonical names. */
    public static final Map<String, String> STATE_CODES = Map.ofEntries(
            Map.entry("01", "JAMMU AND KASHMIR"),
            Map.entry("02", "HIMACHAL PRADESH"),
            Map.entry("03", "PUNJAB"),
            Map.entry("04", "CHANDIGARH"),
            Map.entry("05", "UTTARAKHAND"),
            Map.entry("06", "HARYANA"),
            Map.entry("07", "DELHI"),
            Map.entry("08", "RAJASTHAN"),
            Map.entry("09", "UTTAR PRADESH"),
            Map.entry("10", "BIHAR"),
            Map.entry("11", "SIKKIM"),
            Map.entry("12", "ARUNACHAL PRADESH"),
            Map.entry("13", "NAGALAND"),
            Map.entry("14", "MANIPUR"),
            Map.entry("15", "MIZORAM"),
            Map.entry("16", "TRIPURA"),
            Map.entry("17", "MEGHALAYA"),
            Map.entry("18", "ASSAM"),
            Map.entry("19", "WEST BENGAL"),
            Map.entry("20", "JHARKHAND"),
            Map.entry("21", "ODISHA"),
            Map.entry("22", "CHHATTISGARH"),
            Map.entry("23", "MADHYA PRADESH"),
            Map.entry("24", "GUJARAT"),
            Map.entry("25", "DAMAN AND DIU"),
            Map.entry("26", "DADRA AND NAGAR HAVELI"),
            Map.entry("27", "MAHARASHTRA"),
            Map.entry("28", "ANDHRA PRADESH"),
            Map.entry("29", "KARNATAKA"),
            Map.entry("30", "GOA"),
            Map.entry("31", "LAKSHADWEEP"),
            Map.entry("32", "KERALA"),
            Map.entry("33", "TAMIL NADU"),
            Map.entry("34", "PUDUCHERRY"),
            Map.entry("35", "ANDAMAN AND NICOBAR ISLANDS"),
            Map.entry("36", "TELANGANA"),
            Map.entry("37", "ANDHRA PRADESH"),
            Map.entry("38", "LADAKH"),
            Map.entry("97", "OTHER TERRITORY")
    );

    private static final String GSTIN_REGEX =
            "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][1-9A-Z]Z[0-9A-Z]$";

    public boolean isValidGstinFormat(String gstin) {
        return gstin != null && gstin.matches(GSTIN_REGEX);
    }

    public String extractStateCode(String gstin) {
        return gstin.substring(0, 2);
    }

    public boolean isKnownStateCode(String code) {
        return STATE_CODES.containsKey(code);
    }

    public String stateNameFor(String code) {
        return STATE_CODES.getOrDefault(code, "UNKNOWN");
    }

    /**
     * Core B2B tax routing rule:
     *   doctorStateCode == platformStateCode  →  CGST + SGST (intra-state)
     *   otherwise                             →  IGST (inter-state)
     */
    public TaxType determineTaxType(String doctorStateCode, String platformStateCode) {
        return doctorStateCode.equals(platformStateCode) ? TaxType.CGST_SGST : TaxType.IGST;
    }

    public TaxBreakdown compute(BigDecimal baseAmount, TaxType taxType) {
        if (taxType == TaxType.CGST_SGST) {
            BigDecimal cgst  = pct(baseAmount, CGST_RATE);
            BigDecimal sgst  = pct(baseAmount, SGST_RATE);
            BigDecimal total = baseAmount.add(cgst).add(sgst);
            return new TaxBreakdown(taxType,
                    CGST_RATE, cgst,
                    SGST_RATE, sgst,
                    BigDecimal.ZERO, BigDecimal.ZERO,
                    total);
        } else {
            BigDecimal igst  = pct(baseAmount, IGST_RATE);
            BigDecimal total = baseAmount.add(igst);
            return new TaxBreakdown(taxType,
                    BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO,
                    IGST_RATE, igst,
                    total);
        }
    }

    private static BigDecimal pct(BigDecimal base, BigDecimal rate) {
        return base.multiply(rate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
