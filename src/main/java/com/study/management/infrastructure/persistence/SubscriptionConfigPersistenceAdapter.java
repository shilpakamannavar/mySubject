package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.SubscriptionConfig;
import com.study.management.domain.port.out.SubscriptionConfigPort;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionConfigPersistenceAdapter implements SubscriptionConfigPort {

    private final JpaSubscriptionConfigRepository repository;

    public SubscriptionConfigPersistenceAdapter(JpaSubscriptionConfigRepository repository) {
        this.repository = repository;
    }

    @Override
    public SubscriptionConfig find() {
        return repository.findById(1L).orElse(null);
    }

    @Override
    public SubscriptionConfig save(SubscriptionConfig config) {
        return repository.save(config);
    }
}
