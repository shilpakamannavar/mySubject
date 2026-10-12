package com.study.management.billing.infrastructure.payment;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.study.management.billing.domain.port.out.GstPaymentGatewayPort;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Razorpay adapter for B2B GST orders.
 * Active only when spring.profiles.active=razorpay.
 *
 * The doctor's GSTIN, business name, and tax invoice number are written into
 * Razorpay's notes object.  This links the payment trail to the tax entity,
 * satisfying B2B audit and GSTR-2B ITC matching requirements.
 */
@Component
@Profile("razorpay")
public class GstRazorpayAdapter implements GstPaymentGatewayPort {

    private final RazorpayClient razorpayClient;

    @Value("${razorpay.currency:INR}")
    private String currency;

    @Value("${razorpay.callback.backend-url:http://localhost:8080}")
    private String backendUrl;

    public GstRazorpayAdapter(RazorpayClient razorpayClient) {
        this.razorpayClient = razorpayClient;
    }

    @Override
    public GstOrderResult createOrder(BigDecimal totalAmountInRupees, String gstin,
                                      String businessName, String taxInvoiceNumber) {
        try {
            long amountInPaise = totalAmountInRupees
                    .multiply(BigDecimal.valueOf(100))
                    .longValue();

            JSONObject notes = new JSONObject()
                    .put("gstin",            gstin)
                    .put("business_name",    businessName)
                    .put("tax_invoice_no",   taxInvoiceNumber)
                    .put("payment_type",     "B2B_CORPORATE");

            JSONObject orderRequest = new JSONObject()
                    .put("amount",          amountInPaise)
                    .put("currency",        currency)
                    .put("receipt",         taxInvoiceNumber)
                    .put("payment_capture", 1)
                    .put("callback_url",    backendUrl + "/api/billing/payment/callback")
                    .put("callback_method", "post")
                    .put("notes",           notes);

            Order order = razorpayClient.orders.create(orderRequest);
            return new GstOrderResult(order.get("id"), currency, amountInPaise);

        } catch (RazorpayException e) {
            throw new IllegalStateException("Razorpay B2B order creation failed: " + e.getMessage(), e);
        }
    }
}
