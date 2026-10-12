package com.study.management.application;

import com.study.management.domain.model.SubscriptionConfig;
import com.study.management.domain.port.in.SubscriptionConfigUseCase;
import com.study.management.domain.port.out.SubscriptionConfigPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class SubscriptionConfigApplicationService implements SubscriptionConfigUseCase {

    private final SubscriptionConfigPort configPort;

    public SubscriptionConfigApplicationService(SubscriptionConfigPort configPort) {
        this.configPort = configPort;
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionConfig getConfig() {
        SubscriptionConfig config = configPort.find();
        if (config == null) {
            config = new SubscriptionConfig();
        }
        return config;
    }

    @Override
    public SubscriptionConfig updateMonthlyRate(BigDecimal newRate, String updatedBy) {
        if (newRate == null || newRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Monthly rate must be greater than zero.");
        }
        SubscriptionConfig config = configPort.find();
        if (config == null) {
            config = new SubscriptionConfig();
        }
        config.setMonthlyRate(newRate);
        config.setUpdatedBy(updatedBy);
        return configPort.save(config);
    }
}
