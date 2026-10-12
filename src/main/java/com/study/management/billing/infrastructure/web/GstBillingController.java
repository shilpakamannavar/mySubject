package com.study.management.billing.infrastructure.web;

import com.study.management.billing.domain.model.TaxInvoice;
import com.study.management.billing.domain.port.in.GstBillingUseCase;
import com.study.management.billing.domain.port.in.GstrReportUseCase;
import com.study.management.billing.infrastructure.web.dto.GstCheckoutRequest;
import com.study.management.billing.infrastructure.web.dto.GstCheckoutResponse;
import com.study.management.billing.infrastructure.web.dto.TaxInvoiceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST adapter for the B2B GST billing module.
 *
 *  POST /api/billing/gstin/validate          — validate GSTIN + state consistency
 *  POST /api/billing/checkout                — full checkout: validate, compute tax, create Razorpay order
 *  GET  /api/billing/invoice/{number}        — retrieve invoice by sequential number
 *  GET  /api/billing/invoice/by-order/{id}   — retrieve invoice by Razorpay order ID
 *  GET  /api/billing/gstr1/{year}/{month}    — download GSTR-1 CSV (admin use)
 */
@Tag(name = "GST Billing", description = "B2B GST invoice and Razorpay payment endpoints for doctors/hospitals")
@RestController
@RequestMapping("/api/billing")
public class GstBillingController {

    private final GstBillingUseCase billingUseCase;
    private final GstrReportUseCase gstrReportUseCase;

    @Value("${billing.supplier.name:Clinical Study Platform Pvt Ltd}")
    private String supplierName;

    @Value("${billing.supplier.gstin:27AAAAA0000A1Z5}")
    private String supplierGstin;

    @Value("${billing.supplier.address:}")
    private String supplierAddress;

    @Value("${billing.supplier.state:Maharashtra}")
    private String supplierState;

    @Value("${billing.supplier.email:}")
    private String supplierEmail;

    public GstBillingController(GstBillingUseCase billingUseCase,
                                GstrReportUseCase gstrReportUseCase) {
        this.billingUseCase    = billingUseCase;
        this.gstrReportUseCase = gstrReportUseCase;
    }

    @Operation(
        summary     = "Validate GSTIN",
        description = "Validates the 15-character GSTIN format and checks that the first-2-digit " +
                      "state code matches the declared billing state. Blocks mismatches to prevent " +
                      "faulty B2B GST filing. Returns state code and canonical state name on success."
    )
    @PostMapping("/gstin/validate")
    public ResponseEntity<Map<String, Object>> validateGstin(
            @RequestParam String gstin,
            @RequestParam(required = false) String billingState) {

        GstBillingUseCase.GstinValidateResult result =
                billingUseCase.validateGstin(gstin.toUpperCase().trim(), billingState);

        if (result.valid())
            return ResponseEntity.ok(Map.of(
                    "valid",     true,
                    "stateCode", result.stateCode(),
                    "stateName", result.stateName()));

        return ResponseEntity.badRequest().body(Map.of(
                "valid",        false,
                "errorMessage", result.errorMessage()));
    }

    @Operation(
        summary     = "Initiate GST checkout",
        description = "Full B2B checkout pipeline: validates GSTIN, determines CGST+SGST or IGST, " +
                      "creates a sequentially-numbered DRAFT TAX INVOICE, and creates a Razorpay order " +
                      "with the doctor's GSTIN and business name embedded in the notes object. " +
                      "The frontend uses razorpayOrderId + amountInPaise to render the checkout widget."
    )
    @PostMapping("/checkout")
    public ResponseEntity<GstCheckoutResponse> checkout(
            @Valid @RequestBody GstCheckoutRequest req) {

        GstBillingUseCase.CheckoutCommand cmd = new GstBillingUseCase.CheckoutCommand(
                req.getPhoneNumber(),
                req.getBusinessName(),
                req.getGstin().toUpperCase().trim(),
                req.getBillingState(),
                req.getRegisteredAddress(),
                req.getBusinessEmail(),
                req.getBaseAmount(),
                req.getServiceDescription(),
                req.getSacCode(),
                req.getReferenceId(),
                req.getSubscriptionMonths()
        );

        TaxInvoice invoice = billingUseCase.initiateCheckout(cmd);
        return ResponseEntity.ok(GstCheckoutResponse.from(invoice));
    }

    @Operation(
        summary     = "Get tax invoice by number",
        description = "Returns the full tax invoice including supplier identity, GST breakdown, " +
                      "billing profile, and payment status. Invoice numbers are sequential: TI/{YYYY-MM}/{NNNN}."
    )
    @GetMapping("/invoice/{invoiceNumber}")
    public ResponseEntity<TaxInvoiceResponse> getInvoice(@PathVariable String invoiceNumber) {
        TaxInvoice invoice = billingUseCase.getInvoice(invoiceNumber);
        return ResponseEntity.ok(toResponse(invoice));
    }

    @Operation(
        summary     = "Get tax invoice by Razorpay order ID",
        description = "Useful for frontend confirmation screens after Razorpay checkout completes. " +
                      "Returns PAID status once the webhook has processed payment.captured."
    )
    @GetMapping("/invoice/by-order/{orderId}")
    public ResponseEntity<TaxInvoiceResponse> getInvoiceByOrder(@PathVariable String orderId) {
        TaxInvoice invoice = billingUseCase.getInvoiceByOrderId(orderId);
        return ResponseEntity.ok(toResponse(invoice));
    }

    @Operation(
        summary     = "Download GSTR-1 CSV",
        description = "Generates a GSTR-1 compliant CSV for all PAID invoices in the specified month. " +
                      "Entries are sorted by Place of Supply (state code) for easy filing segregation. " +
                      "Intended for finance/admin use at month-end."
    )
    @GetMapping("/gstr1/{year}/{month}")
    public ResponseEntity<byte[]> downloadGstr1(
            @PathVariable @Min(2020) @Max(2099) int year,
            @PathVariable @Min(1)   @Max(12)   int month) {

        String csv      = gstrReportUseCase.generateGstr1Csv(year, month);
        String filename = String.format("GSTR1_%d_%02d.csv", year, month);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.getBytes());
    }

    private TaxInvoiceResponse toResponse(TaxInvoice invoice) {
        return TaxInvoiceResponse.from(invoice,
                supplierName, supplierGstin, supplierAddress, supplierState, supplierEmail);
    }
}
