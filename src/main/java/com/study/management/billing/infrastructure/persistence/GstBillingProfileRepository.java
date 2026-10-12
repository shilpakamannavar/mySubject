package com.study.management.billing.infrastructure.persistence;

import com.study.management.billing.domain.model.GstBillingProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GstBillingProfileRepository extends JpaRepository<GstBillingProfile, Long> {

    Optional<GstBillingProfile> findByGstin(String gstin);
}
