package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.PromoCodeUsageLog;
import com.study.management.domain.port.out.PromoCodeUsagePort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PromoCodeUsagePersistenceAdapter implements PromoCodeUsagePort {

    private final JpaPromoCodeUsageRepository jpa;

    public PromoCodeUsagePersistenceAdapter(JpaPromoCodeUsageRepository jpa) {
        this.jpa = jpa;
    }

    @Override public PromoCodeUsageLog       save(PromoCodeUsageLog log)     { return jpa.save(log); }
    @Override public List<PromoCodeUsageLog> findAll()                        { return jpa.findAll(); }
    @Override public List<PromoCodeUsageLog> findByPromoCode(String code)     { return jpa.findByPromoCodeIgnoreCase(code); }
    @Override public List<PromoCodeUsageLog> findByCustomerId(Long customerId){ return jpa.findByCustomer_Id(customerId); }
}