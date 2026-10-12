package com.study.management.infrastructure.cognito;

import com.study.management.infrastructure.web.dto.CognitoSyncReport;
import com.study.management.infrastructure.web.dto.RoleSyncSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Runs once, after the full Spring context (datasource, JPA, all beans) is ready.
 * Pulls every member of each configured Cognito group into the corresponding DB table.
 * Errors from Cognito (wrong credentials, bad group name) are logged but never
 * prevent the application from starting.
 */
@Component
public class CognitoStartupSync implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CognitoStartupSync.class);

    private static final List<String> ROLES_TO_SYNC = List.of(
            "primary-investigator",
            "co-investigator",
            "crc",
            "cro",
            "hospital",
            "subject"
    );

    private final CognitoSyncService cognitoSyncService;

    public CognitoStartupSync(CognitoSyncService cognitoSyncService) {
        this.cognitoSyncService = cognitoSyncService;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("=== Cognito startup sync starting ===");

        for (String role : ROLES_TO_SYNC) {
            try {
                CognitoSyncReport report = cognitoSyncService.syncSingleRole(role);
                RoleSyncSummary s = report.getSummary().get(role);

                if (s != null) {

                    log.info("[{}] scanned={} created={} skipped={} errors={}",
                             role,
                             report.getTotalUsersScanned(),
                             s.getCreated(),
                             s.getSkipped(),
                             s.getErrors());

                    // log each ERROR detail so the developer can fix it immediately
                    report.getDetails().stream()
                            .filter(d -> "ERROR".equals(d.getAction()) || "GROUP_NOT_FOUND".equals(d.getAction()))
                            .forEach(d -> log.warn("  [{}] {} → {} : {}",
                                                   role, d.getUsername(), d.getAction(), d.getMessage()));
                }
            } catch (Exception e) {
                // never let a Cognito failure prevent the app from starting
                log.error("[{}] startup sync failed: {}", role, e.getMessage(), e);
            }
        }

        log.info("=== Cognito startup sync complete ===");
    }
}
