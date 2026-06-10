package com.study.management.domain.model;

public enum DiscountType {
    FULL,        // 100% waiver — no payment gateway call
    PERCENTAGE,  // e.g., 20% off total (base + GST)
    FLAT         // e.g., ₹1000 flat deduction
}