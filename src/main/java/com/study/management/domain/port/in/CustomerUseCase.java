package com.study.management.domain.port.in;

import com.study.management.domain.model.Customer;

import java.util.List;

public interface CustomerUseCase {
    Customer createCustomer(Customer customer);
    Customer getById(Long id);
    List<Customer> getAll();
}