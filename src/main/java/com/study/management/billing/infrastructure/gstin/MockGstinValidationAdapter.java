package com.study.management.billing.infrastructure.gstin;

import com.study.management.billing.domain.port.out.GstinValidationPort;
import com.study.management.billing.domain.service.GstTaxEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Offline GSTIN validation adapter (default — no external API key required).
 *
 * Validates the 15-character GSTIN format and extracts the state code.
 * In production, replace or complement with a live adapter that calls:
 *   • Razorpay KYC/Validation toolkit, or
 *   • ClearTax / Sandbox GSTIN verification API
 *
 * To swap: implement GstinValidationPort, annotate @Primary, and set
 *   billing.gstin.validation.url=https://... in application.properties.
 */
@Component
public class MockGstinValidationAdapter implements GstinValidationPort {

    private static final Logger log = LoggerFactory.getLogger(MockGstinValidationAdapter.class);

    private final GstTaxEngine taxEngine;

    public MockGstinValidationAdapter(GstTaxEngine taxEngine) {
        this.taxEngine = taxEngine;
    }

    @Override
    public GstinValidationResult validate(String gstin) {
        if (!taxEngine.isValidGstinFormat(gstin))
            return new GstinValidationResult(false, null, null,
                    "GSTIN does not match the required 15-character GST format.");

        String stateCode = taxEngine.extractStateCode(gstin);

        if (!taxEngine.isKnownStateCode(stateCode))
            return new GstinValidationResult(false, stateCode, null,
                    "State code '" + stateCode + "' is not a recognised Indian GST state/UT code.");

        String legalName = "VALIDATED (offline mock — no live registry call)";
        log.info("[GSTIN-MOCK] {} → state={} ({})", gstin, stateCode, taxEngine.stateNameFor(stateCode));

        return new GstinValidationResult(true, stateCode, legalName, null);
    }
}
