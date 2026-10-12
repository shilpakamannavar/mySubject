package com.study.management.billing.infrastructure.lookup;

import com.study.management.billing.domain.port.out.HospitalLookupPort;
import com.study.management.domain.port.out.HospitalPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class HospitalLookupAdapter implements HospitalLookupPort {

    private final HospitalPort hospitalPort;

    public HospitalLookupAdapter(HospitalPort hospitalPort) {
        this.hospitalPort = hospitalPort;
    }

    @Override
    public Optional<HospitalSummary> findById(UUID hospitalId) {
        return hospitalPort.findById(hospitalId)
                .map(h -> new HospitalSummary(
                        h.getHospitalId(),
                        h.getEmail(),
                        h.getHospitalName()));
    }

    @Override
    public Optional<HospitalSummary> findByPhone(String phone) {
        return hospitalPort.findByPhone(phone)
                .map(h -> new HospitalSummary(
                        h.getHospitalId(),
                        h.getEmail(),
                        h.getHospitalName()));
    }
}
