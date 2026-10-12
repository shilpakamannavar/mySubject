package com.study.management.application;

import com.study.management.domain.model.AuditLogEntry;
import com.study.management.domain.port.in.AuditUseCase;
import com.study.management.domain.port.out.AuditPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AuditApplicationService implements AuditUseCase {

    private final AuditPort auditPort;

    public AuditApplicationService(AuditPort auditPort) {
        this.auditPort = auditPort;
    }

    @Override
    public Page<AuditLogEntry> queryAuditLogs(String entityType, UUID entityId, String action,
                                               OffsetDateTime startDate, OffsetDateTime endDate,
                                               UUID performedBy, Pageable pageable) {
        return auditPort.searchLogs(entityType, entityId, action, performedBy, startDate, endDate, pageable);
    }
}
