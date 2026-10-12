package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.AuditLogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.UUID;

interface JpaAuditLogRepository extends JpaRepository<AuditLogEntry, Long> {
    Page<AuditLogEntry> findByEntityTypeAndEntityId(String entityType, UUID entityId, Pageable pageable);

    @Query("SELECT a FROM AuditLogEntry a WHERE " +
           "(:entityType IS NULL OR a.entityType = :entityType) AND " +
           "(:entityId IS NULL OR a.entityId = :entityId) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:performedBy IS NULL OR a.performedBy = :performedBy) AND " +
           "(:startDate IS NULL OR a.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR a.createdAt <= :endDate)")
    Page<AuditLogEntry> searchAuditLogs(@Param("entityType") String entityType,
                                         @Param("entityId") UUID entityId,
                                         @Param("action") String action,
                                         @Param("performedBy") UUID performedBy,
                                         @Param("startDate") OffsetDateTime startDate,
                                         @Param("endDate") OffsetDateTime endDate,
                                         Pageable pageable);
}
