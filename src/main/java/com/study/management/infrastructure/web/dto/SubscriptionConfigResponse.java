package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.SubscriptionConfig;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class SubscriptionConfigResponse {

    private BigDecimal monthlyRate;
    private OffsetDateTime updatedAt;
    private String updatedBy;

    public static SubscriptionConfigResponse from(SubscriptionConfig c) {
        SubscriptionConfigResponse r = new SubscriptionConfigResponse();
        r.monthlyRate = c.getMonthlyRate();
        r.updatedAt   = c.getUpdatedAt();
        r.updatedBy   = c.getUpdatedBy();
        return r;
    }

    public BigDecimal getMonthlyRate()              { return monthlyRate; }
    public OffsetDateTime getUpdatedAt()            { return updatedAt; }
    public String getUpdatedBy()                    { return updatedBy; }
}
