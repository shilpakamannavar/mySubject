package com.study.management.domain.port.in;

import com.study.management.domain.model.SubscriptionConfig;
import java.math.BigDecimal;

public interface SubscriptionConfigUseCase {
    SubscriptionConfig getConfig();
    SubscriptionConfig updateMonthlyRate(BigDecimal newRate, String updatedBy);
}
