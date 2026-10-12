package com.study.management.infrastructure.web;

import com.study.management.domain.model.Customer;
import com.study.management.domain.port.in.CustomerUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Customers", description = "Study sponsors / researchers who own studies and redeem promo codes")
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerUseCase customerUseCase;

    public CustomerController(CustomerUseCase customerUseCase) {
        this.customerUseCase = customerUseCase;
    }

    @Operation(summary = "Register a new customer")
    @PostMapping
    public ResponseEntity<Customer> create(@Valid @RequestBody Customer customer) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerUseCase.createCustomer(customer));
    }

    @Operation(summary = "Get customer by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getById(@PathVariable Long id) {
        return ResponseEntity.ok(customerUseCase.getById(id));
    }

    @Operation(summary = "List all customers")
    @GetMapping
    public ResponseEntity<List<Customer>> listAll() {
        return ResponseEntity.ok(customerUseCase.getAll());
    }
}