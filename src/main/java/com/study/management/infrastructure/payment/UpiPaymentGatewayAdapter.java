package com.study.management.infrastructure.payment;

import com.study.management.domain.model.Money;
import com.study.management.domain.port.out.PaymentGatewayPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * OUTPUT PORT ADAPTER — UPI payment gateway.
 * Swap with RazorpayAdapter / PayUAdapter / BillDeskAdapter by implementing
 * PaymentGatewayPort and marking the other as @Primary or using Spring profiles.
 */
@Component
@Profile("!razorpay")
public class UpiPaymentGatewayAdapter implements PaymentGatewayPort {

    @Override
    public String initiatePayment(Money amount) {
        // Production: call UPI provider REST API here
        String txnRef = "UPI_TXN_"
                + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        System.out.printf("UPI payment initiated | amount=%s | ref=%s%n", amount, txnRef);
        return txnRef;
    }
}