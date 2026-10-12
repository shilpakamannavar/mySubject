package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.Customer;
import com.study.management.domain.port.out.CustomerPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomerPersistenceAdapter implements CustomerPort {

    private final JpaCustomerRepository jpa;

    public CustomerPersistenceAdapter(JpaCustomerRepository jpa) {
        this.jpa = jpa;
    }

    @Override public Customer             save(Customer c)        { return jpa.save(c); }
    @Override public Optional<Customer>   findById(Long id)       { return jpa.findById(id); }
    @Override public Optional<Customer>   findByEmail(String e)   { return jpa.findByEmailIgnoreCase(e); }
    @Override public List<Customer>       findAll()               { return jpa.findAll(); }
    @Override public boolean              existsByEmail(String e)  { return jpa.existsByEmailIgnoreCase(e); }
}
