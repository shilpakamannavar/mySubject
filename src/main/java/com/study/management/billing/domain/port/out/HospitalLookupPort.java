package com.study.management.billing.domain.port.out;

import java.util.Optional;
import java.util.UUID;

public interface HospitalLookupPort {

    record HospitalSummary(UUID hospitalId, String email, String hospitalName) {}

    /**
     * Fetches the minimum Hospital/SMO data the billing module needs.
     * Delegates to the existing HospitalPort without leaking full hospital internals.
     */
    Optional<HospitalSummary> findById(UUID hospitalId);

    Optional<HospitalSummary> findByPhone(String phone);
}
