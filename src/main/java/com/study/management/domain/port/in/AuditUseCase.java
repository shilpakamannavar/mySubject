package com.study.management.domain.port.in;

import com.study.management.domain.model.AuditLogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface AuditUseCase {
    Page<AuditLogEntry> queryAuditLogs(String entityType, UUID entityId, String action,
                                        OffsetDateTime startDate, OffsetDateTime endDate,
                                        UUID performedBy, Pageable pageable);
}
