package com.study.management.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Immutable value object representing a monetary amount in INR.
 * All arithmetic operations return new instances; amount never goes below ₹0.
 */
public final class Money {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    private final BigDecimal amount;

    private Money(BigDecimal amount) {
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money of(BigDecimal amount) { return new Money(amount); }
    public static Money of(long units)        { return new Money(BigDecimal.valueOf(units)); }

    public Money add(Money other)      { return new Money(this.amount.add(other.amount)); }
    public Money multiply(BigDecimal f){ return new Money(this.amount.multiply(f)); }

    /** Subtracts other from this; floors at ₹0. */
    public Money subtract(Money other) {
        return new Money(this.amount.subtract(other.amount).max(BigDecimal.ZERO));
    }

    public boolean isZero()                  { return amount.compareTo(BigDecimal.ZERO) == 0; }
    public boolean isGreaterThan(Money other){ return amount.compareTo(other.amount) > 0; }
    public BigDecimal getAmount()            { return amount; }

    @Override public String toString()       { return "₹" + amount; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Money m)) return false;
        return amount.compareTo(m.amount) == 0;
    }

    @Override public int hashCode() { return amount.stripTrailingZeros().hashCode(); }
}