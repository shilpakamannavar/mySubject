package com.study.management.application;

import com.study.management.domain.model.CoInvestigator;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.domain.port.in.CoInvestigatorUseCase;
import com.study.management.domain.port.out.CoInvestigatorPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CoInvestigatorApplicationService implements CoInvestigatorUseCase {

    private final CoInvestigatorPort coInvestigatorPort;

    public CoInvestigatorApplicationService(CoInvestigatorPort coInvestigatorPort) {
        this.coInvestigatorPort = coInvestigatorPort;
    }

    @Override
    public CoInvestigator create(CoInvestigator ci) {
        if (ci.getEmail() != null && coInvestigatorPort.existsByEmail(ci.getEmail())) {
            throw new IllegalArgumentException("Co-Investigator with email '" + ci.getEmail() + "' already exists.");
        }
        if (ci.getPhone() != null && coInvestigatorPort.existsByPhone(ci.getPhone())) {
            throw new IllegalArgumentException("Co-Investigator with phone '" + ci.getPhone() + "' already exists.");
        }
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String suffix   = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        ci.setCiNumber("CI-" + datePart + "-" + suffix);
        return coInvestigatorPort.save(ci);
    }

    @Override
    @Transactional(readOnly = true)
    public CoInvestigator getById(UUID id) {
        return coInvestigatorPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CoInvestigator", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoInvestigator> listAll() {
        return coInvestigatorPort.findAll();
    }
}