package com.study.management.application;

import com.study.management.domain.model.ContractResearchOrganization;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.domain.port.in.CroUseCase;
import com.study.management.domain.port.out.CroPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CroApplicationService implements CroUseCase {

    private final CroPort croPort;

    public CroApplicationService(CroPort croPort) {
        this.croPort = croPort;
    }

    @Override
    public ContractResearchOrganization create(ContractResearchOrganization cro) {
        if (cro.getEmail() != null && croPort.existsByEmail(cro.getEmail())) {
            throw new IllegalArgumentException("CRO with email '" + cro.getEmail() + "' already exists.");
        }
        if (cro.getPhone() != null && croPort.existsByPhone(cro.getPhone())) {
            throw new IllegalArgumentException("CRO with phone '" + cro.getPhone() + "' already exists.");
        }
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String suffix   = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        cro.setCroNumber("CRO-" + datePart + "-" + suffix);
        return croPort.save(cro);
    }

    @Override
    @Transactional(readOnly = true)
    public ContractResearchOrganization getById(UUID id) {
        return croPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContractResearchOrganization", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractResearchOrganization> listAll() {
        return croPort.findAll();
    }
}