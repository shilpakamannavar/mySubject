package com.study.management.billing.infrastructure.persistence;

import com.study.management.billing.domain.model.GstBillingProfile;
import com.study.management.billing.domain.port.out.GstBillingProfilePort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GstBillingProfileAdapter implements GstBillingProfilePort {

    private final GstBillingProfileRepository repo;

    public GstBillingProfileAdapter(GstBillingProfileRepository repo) {
        this.repo = repo;
    }

    @Override
    public GstBillingProfile save(GstBillingProfile profile) {
        return repo.save(profile);
    }

    @Override
    public Optional<GstBillingProfile> findByGstin(String gstin) {
        return repo.findByGstin(gstin);
    }
}
