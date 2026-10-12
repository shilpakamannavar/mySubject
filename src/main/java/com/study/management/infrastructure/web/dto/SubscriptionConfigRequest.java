package com.study.management.infrastructure.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class SubscriptionConfigRequest {

    @NotNull
    @DecimalMin(value = "1.00", message = "Monthly rate must be at least ₹1.00")
    private BigDecimal monthlyRate;

    public BigDecimal getMonthlyRate()              { return monthlyRate; }
    public void setMonthlyRate(BigDecimal v)        { this.monthlyRate = v; }
}
