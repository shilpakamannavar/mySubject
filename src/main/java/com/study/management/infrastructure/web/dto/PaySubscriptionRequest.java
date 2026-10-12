package com.study.management.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

public class PaySubscriptionRequest {

    @Schema(description = "Optional payment reference / transaction ID. Auto-generated if omitted.")
    private String paymentReference;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Payment date — used as subscription start date. Defaults to today if omitted.", example = "2026-09-19")
    private LocalDate paymentDate;

    @Min(1) @Max(36)
    @Schema(description = "Subscription duration in months (1–36). Defaults to 1. Determines subscription end date and total charge.", example = "3")
    private Integer durationMonths = 1;

    public String getPaymentReference()              { return paymentReference; }
    public void setPaymentReference(String v)        { this.paymentReference = v; }

    public LocalDate getPaymentDate()               { return paymentDate; }
    public void setPaymentDate(LocalDate v)         { this.paymentDate = v; }

    public Integer getDurationMonths()              { return durationMonths; }
    public void setDurationMonths(Integer v)        { this.durationMonths = v; }
}
