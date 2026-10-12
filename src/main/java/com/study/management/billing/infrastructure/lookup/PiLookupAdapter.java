package com.study.management.billing.infrastructure.lookup;

import com.study.management.billing.domain.port.out.PiLookupPort;
import com.study.management.domain.port.out.InvestigatorPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PiLookupAdapter implements PiLookupPort {

    private final InvestigatorPort investigatorPort;

    public PiLookupAdapter(InvestigatorPort investigatorPort) {
        this.investigatorPort = investigatorPort;
    }

    @Override
    public Optional<PiSummary> findById(UUID piId) {
        return investigatorPort.findById(piId)
                .map(pi -> new PiSummary(
                        pi.getPiId(),
                        pi.getEmail(),
                        (pi.getFirstName() + " " + pi.getLastName()).trim()));
    }

    @Override
    public Optional<PiSummary> findByPhone(String phone) {
        return investigatorPort.findByPhone(phone)
                .map(pi -> new PiSummary(
                        pi.getPiId(),
                        pi.getEmail(),
                        (pi.getFirstName() + " " + pi.getLastName()).trim()));
    }
}
