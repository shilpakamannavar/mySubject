package com.study.management.infrastructure.web.dto;

public class PromoCodeAssignmentRequest {

    /** null means assign to ALL customers */
    private Long customerId;

    public Long getCustomerId()              { return customerId; }
    public void setCustomerId(Long id)       { this.customerId = id; }
}
