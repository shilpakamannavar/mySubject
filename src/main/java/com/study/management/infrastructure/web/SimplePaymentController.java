package com.study.management.infrastructure.web;

import com.study.management.domain.model.Invoice;
import com.study.management.domain.model.Study;
import com.study.management.domain.port.in.PaymentUseCase;
import com.study.management.domain.port.in.StudyUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Tag(name = "Simple Payment", description = "Initiate and confirm UPI payments for studies")
@RestController
@RequestMapping("/api/payment")
public class SimplePaymentController {

    private final PaymentUseCase paymentUseCase;
    private final StudyUseCase   studyUseCase;

    public SimplePaymentController(PaymentUseCase paymentUseCase, StudyUseCase studyUseCase) {
        this.paymentUseCase = paymentUseCase;
        this.studyUseCase   = studyUseCase;
    }

    @Operation(summary = "Initiate payment for a study — returns transactionId")
    @PostMapping("/initiate/{studyId}")
    public ResponseEntity<Map<String, Object>> initiate(@PathVariable Long studyId) {
        Study study = studyUseCase.getById(studyId);
        Invoice invoice = paymentUseCase.proceedForPayment(studyId, null);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("studyId", study.getStudyId());
        body.put("transactionId", invoice.getPaymentReference());
        body.put("baseFee", invoice.getBaseFee());
        body.put("gst", invoice.getGst());
        body.put("totalAmount", invoice.getTotalAmount());
        body.put("status", invoice.getStatus());
        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Confirm UPI payment — activates study on success")
    @PostMapping("/confirm")
    public ResponseEntity<Map<String, Object>> confirm(
            @RequestParam String transactionId,
            @RequestParam String upiRef) {

        Study study = studyUseCase.getByPaymentReference(transactionId);
        paymentUseCase.confirmPayment(study.getId(), upiRef);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("studyId", study.getStudyId());
        body.put("transactionId", transactionId);
        body.put("upiRef", upiRef);
        body.put("message", "Payment confirmed. Study is now ACTIVE.");
        return ResponseEntity.ok(body);
    }
}
