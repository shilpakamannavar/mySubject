package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.SubscriptionConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSubscriptionConfigRepository extends JpaRepository<SubscriptionConfig, Long> {
}
