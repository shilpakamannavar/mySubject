package com.study.management.billing.domain.port.out;

import java.util.Optional;
import java.util.UUID;

public interface PiLookupPort {

    record PiSummary(UUID piId, String email, String fullName) {}

    /**
     * Fetches the minimum PI data the billing module needs — identity, email, name.
     * Delegates to the existing InvestigatorPort without leaking full PI internals.
     */
    Optional<PiSummary> findById(UUID piId);

    Optional<PiSummary> findByPhone(String phone);
}
