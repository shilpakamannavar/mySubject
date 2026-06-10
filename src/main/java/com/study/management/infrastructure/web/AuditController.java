package com.study.management.infrastructure.web;

import com.study.management.domain.model.AuditLogEntry;
import com.study.management.domain.port.in.AuditUseCase;
import com.study.management.infrastructure.web.dto.AuditLogItem;
import com.study.management.infrastructure.web.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Audit", description = "Audit log query")
@RestController
@RequestMapping("/api/v1/audit")
public class AuditController {

    private final AuditUseCase auditUseCase;

    public AuditController(AuditUseCase auditUseCase) {
        this.auditUseCase = auditUseCase;
    }

    @Operation(summary = "Query audit logs")
    @GetMapping
    public ResponseEntity<PagedResponse<AuditLogItem>> queryAuditLogs(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) UUID entityId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate,
            @RequestParam(required = false) UUID performedBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<AuditLogEntry> logPage = auditUseCase.queryAuditLogs(
                entityType, entityId, action, startDate, endDate, performedBy,
                PageRequest.of(page - 1, size));

        List<AuditLogItem> items = logPage.getContent().stream()
                .map(this::toItem).collect(Collectors.toList());
        return ResponseEntity.ok(PagedResponse.of(items, page, size, logPage.getTotalElements()));
    }

    private AuditLogItem toItem(AuditLogEntry e) {
        AuditLogItem item = new AuditLogItem();
        item.setAuditId(e.getAuditId());
        item.setEntityType(e.getEntityType());
        item.setEntityId(e.getEntityId());
        item.setAction(e.getAction());
        item.setOldValues(e.getOldValues());
        item.setNewValues(e.getNewValues());
        item.setChangedFields(e.getChangedFields());
        item.setPerformedBy(e.getPerformedBy());
        item.setPerformedByRole(e.getPerformedByRole());
        item.setIpAddress(e.getIpAddress());
        item.setReason(e.getReason());
        item.setCreatedAt(e.getCreatedAt());
        return item;
    }
}
