package com.study.management.billing.domain.port.out;

import com.study.management.billing.domain.model.GstBillingProfile;

import java.util.Optional;

public interface GstBillingProfilePort {

    GstBillingProfile save(GstBillingProfile profile);

    Optional<GstBillingProfile> findByGstin(String gstin);
}
