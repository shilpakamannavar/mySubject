package com.study.management.billing.infrastructure.payment;

import com.study.management.billing.domain.port.out.GstPaymentGatewayPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Mock payment gateway for local development and tests.
 * Returns a fake Razorpay-style order ID without hitting any external API.
 * Active when the "razorpay" profile is NOT active.
 */
@Component
@Profile("!razorpay")
public class MockGstPaymentGatewayAdapter implements GstPaymentGatewayPort {

    private static final Logger log = LoggerFactory.getLogger(MockGstPaymentGatewayAdapter.class);

    @Override
    public GstOrderResult createOrder(BigDecimal totalAmountInRupees, String gstin,
                                      String businessName, String taxInvoiceNumber) {
        long paise   = totalAmountInRupees.multiply(BigDecimal.valueOf(100)).longValue();
        String fakeId = "order_MOCK_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase();

        log.info("[MOCK-GATEWAY] B2B order created: id={} gstin={} business='{}' invoice={} amount=₹{}",
                fakeId, gstin, businessName, taxInvoiceNumber, totalAmountInRupees);

        return new GstOrderResult(fakeId, "INR", paise);
    }
}
