package com.study.management.billing.domain.port.out;

public interface GstinValidationPort {

    record GstinValidationResult(boolean valid, String stateCode, String legalName, String errorMessage) {}

    /**
     * Calls an external (or mock) GSTIN registry to verify the GSTIN exists and is active.
     * Implementations: MockGstinValidationAdapter (offline) or live API adapters (ClearTax, Sandbox).
     */
    GstinValidationResult validate(String gstin);
}
