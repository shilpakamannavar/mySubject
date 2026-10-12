package com.study.management.infrastructure.web;

import com.study.management.infrastructure.cognito.CognitoSyncService;
import com.study.management.infrastructure.web.dto.CognitoGroupInfo;
import com.study.management.infrastructure.web.dto.CognitoSyncReport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Admin", description = "Admin operations")
@RestController
@RequestMapping("/api/v1/admin")
public class CognitoSyncController {

    private final CognitoSyncService cognitoSyncService;

    public CognitoSyncController(CognitoSyncService cognitoSyncService) {
        this.cognitoSyncService = cognitoSyncService;
    }

    @Operation(
        summary = "List Cognito groups (diagnostic)",
        description = "Shows all groups in the Cognito user pool, cross-referenced against the configured " +
                      "group names. Use this FIRST to verify group names before running the sync. " +
                      "Groups marked foundInCognito=false will cause GROUP_NOT_FOUND errors during sync."
    )
    @GetMapping("/sync/cognito/groups")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CognitoGroupInfo>> listCognitoGroups() {
        return ResponseEntity.ok(cognitoSyncService.listGroupsForDiagnostic());
    }

    @Operation(
        summary = "Sync Cognito users to database",
        description = "Fetches users from each configured Cognito group and creates missing records in the " +
                      "primary_investigators, co_investigators, crcs, cros, and subjects tables. " +
                      "Existing records (matched by email) are skipped. Returns a full report. " +
                      "Run GET /api/v1/admin/sync/cognito/groups first to verify group names."
    )
    @PostMapping("/sync/cognito")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CognitoSyncReport> syncCognitoUsers() {
        return ResponseEntity.ok(cognitoSyncService.sync());
    }
}
