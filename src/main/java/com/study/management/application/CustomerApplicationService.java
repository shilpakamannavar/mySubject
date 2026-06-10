package com.study.management.application;

import com.study.management.domain.model.Customer;
import com.study.management.domain.port.in.CustomerUseCase;
import com.study.management.domain.port.out.CustomerPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomerApplicationService implements CustomerUseCase {

    private final CustomerPort customerPort;

    public CustomerApplicationService(CustomerPort customerPort) {
        this.customerPort = customerPort;
    }

    @Override
    public Customer createCustomer(Customer customer) {
        if (customerPort.existsByEmail(customer.getEmail()))
            throw new IllegalArgumentException(
                    "Customer with email '" + customer.getEmail() + "' already exists.");
        return customerPort.save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getById(Long id) {
        return customerPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getAll() {
        return customerPort.findAll();
    }
}