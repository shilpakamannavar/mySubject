package com.study.management.infrastructure.web;

import com.study.management.domain.model.ClinicalPaymentStatus;
import com.study.management.domain.model.PaymentEntity;
import com.study.management.domain.port.in.ClinicalPaymentUseCase;
import com.study.management.infrastructure.web.dto.CreatePaymentRequest;
import com.study.management.infrastructure.web.dto.PaymentResponse;
import com.study.management.infrastructure.web.dto.PaymentWebhookRequest;
import com.study.management.infrastructure.web.dto.PaymentWebhookResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Payments", description = "Clinical study payment management")
@RestController
public class ClinicalPaymentController {

    private final ClinicalPaymentUseCase paymentUseCase;

    public ClinicalPaymentController(ClinicalPaymentUseCase paymentUseCase) {
        this.paymentUseCase = paymentUseCase;
    }

    @Operation(summary = "Create a payment for a study")
    @PostMapping("/api/v1/studies/{studyId}/payments")
    public ResponseEntity<PaymentResponse> createPayment(@PathVariable UUID studyId,
                                                          @RequestBody CreatePaymentRequest req) {
        PaymentEntity payment = new PaymentEntity();
        payment.setPaymentType(req.getPaymentType());
        payment.setRatePerService(req.getRatePerService());
        payment.setGstPercentage(req.getGstPercentage() != null ? req.getGstPercentage() : BigDecimal.ZERO);
        payment.setCurrency(req.getCurrency() != null ? req.getCurrency() : "USD");
        payment.setPaymentMethod(req.getPaymentMethod());
        payment.setInvoiceNumber(req.getInvoiceNumber());
        payment.setDueDate(req.getDueDate());
        payment.setNotes(req.getNotes());
        PaymentEntity saved = paymentUseCase.createPayment(studyId, payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @Operation(summary = "Payment webhook callback")
    @PostMapping("/api/v1/payments/webhook")
    public ResponseEntity<PaymentWebhookResponse> webhook(@RequestBody PaymentWebhookRequest req) {
        paymentUseCase.handleWebhook(req.getPaymentId(),
                req.getPaymentGatewayRef(),
                req.getStatus().name());
        return ResponseEntity.ok(new PaymentWebhookResponse("OK", "Payment processed successfully"));
    }

    @Operation(summary = "Get payment history for a study")
    @GetMapping("/api/v1/studies/{studyId}/payments")
    public ResponseEntity<List<PaymentResponse>> getPayments(@PathVariable UUID studyId) {
        List<PaymentEntity> payments = paymentUseCase.getPaymentsForStudy(studyId);
        return ResponseEntity.ok(payments.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    private PaymentResponse toResponse(PaymentEntity p) {
        PaymentResponse r = new PaymentResponse();
        r.setPaymentId(p.getPaymentId());
        if (p.getStudy() != null) {
            r.setStudyId(p.getStudy().getStudyId());
            r.setStudyName(p.getStudy().getStudyName());
        }
        r.setPaymentType(p.getPaymentType());
        r.setAmount(p.getAmount());
        r.setCurrency(p.getCurrency());
        r.setTaxAmount(p.getTaxAmount());
        r.setTotalAmount(p.getTotalAmount());
        r.setRatePerService(p.getRatePerService());
        r.setGstPercentage(p.getGstPercentage());
        r.setPaymentMethod(p.getPaymentMethod());
        r.setPaymentGatewayRef(p.getPaymentGatewayRef());
        r.setInvoiceNumber(p.getInvoiceNumber());
        r.setPaymentStatus(p.getPaymentStatus());
        r.setPaymentUrl(p.getPaymentUrl());
        r.setPaidAt(p.getPaidAt());
        r.setDueDate(p.getDueDate());
        r.setNotes(p.getNotes());
        r.setCreatedAt(p.getCreatedAt());
        r.setUpdatedAt(p.getUpdatedAt());
        return r;
    }
}
