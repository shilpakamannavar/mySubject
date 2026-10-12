package com.study.management.application;

import com.study.management.domain.model.ClinicalResearchCoordinator;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.domain.port.in.CrcUseCase;
import com.study.management.domain.port.out.CrcPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CrcApplicationService implements CrcUseCase {

    private final CrcPort crcPort;

    public CrcApplicationService(CrcPort crcPort) {
        this.crcPort = crcPort;
    }

    @Override
    public ClinicalResearchCoordinator create(ClinicalResearchCoordinator crc) {
        if (crc.getEmail() != null && crcPort.existsByEmail(crc.getEmail())) {
            throw new IllegalArgumentException("CRC with email '" + crc.getEmail() + "' already exists.");
        }
        if (crc.getPhone() != null && crcPort.existsByPhone(crc.getPhone())) {
            throw new IllegalArgumentException("CRC with phone '" + crc.getPhone() + "' already exists.");
        }
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String suffix   = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        crc.setCrcNumber("CRC-" + datePart + "-" + suffix);
        return crcPort.save(crc);
    }

    @Override
    @Transactional(readOnly = true)
    public ClinicalResearchCoordinator getById(UUID id) {
        return crcPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClinicalResearchCoordinator", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClinicalResearchCoordinator> listAll() {
        return crcPort.findAll();
    }
}