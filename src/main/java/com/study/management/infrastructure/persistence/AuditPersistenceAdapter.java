package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.AuditLogEntry;
import com.study.management.domain.port.out.AuditPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public class AuditPersistenceAdapter implements AuditPort {

    private final JpaAuditLogRepository jpa;

    public AuditPersistenceAdapter(JpaAuditLogRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Page<AuditLogEntry> searchLogs(String entityType, UUID entityId, String action,
                                           UUID performedBy, OffsetDateTime startDate,
                                           OffsetDateTime endDate, Pageable pageable) {
        return jpa.searchAuditLogs(entityType, entityId, action, performedBy, startDate, endDate, pageable);
    }
}
