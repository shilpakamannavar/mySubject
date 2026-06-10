package com.study.management.domain.port.out;

import com.study.management.domain.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerPort {
    Customer save(Customer customer);
    Optional<Customer> findById(Long id);
    Optional<Customer> findByEmail(String email);
    List<Customer> findAll();
    boolean existsByEmail(String email);
}