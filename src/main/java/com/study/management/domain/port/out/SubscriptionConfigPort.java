package com.study.management.domain.port.out;

import com.study.management.domain.model.SubscriptionConfig;

public interface SubscriptionConfigPort {
    SubscriptionConfig find();
    SubscriptionConfig save(SubscriptionConfig config);
}
