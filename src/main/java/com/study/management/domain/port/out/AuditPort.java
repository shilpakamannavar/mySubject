package com.study.management.domain.port.out;

import com.study.management.domain.model.AuditLogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface AuditPort {
    Page<AuditLogEntry> searchLogs(String entityType, UUID entityId, String action,
                                    UUID performedBy, OffsetDateTime startDate,
                                    OffsetDateTime endDate, Pageable pageable);
}
