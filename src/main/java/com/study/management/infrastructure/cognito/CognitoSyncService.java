package com.study.management.infrastructure.cognito;

import com.study.management.domain.model.ClinicalResearchCoordinator;
import com.study.management.domain.model.CoInvestigator;
import com.study.management.domain.model.ContractResearchOrganization;
import com.study.management.domain.model.Hospital;
import com.study.management.domain.model.PrimaryInvestigator;
import com.study.management.domain.model.Subject;
import com.study.management.domain.port.in.CoInvestigatorUseCase;
import com.study.management.domain.port.in.CrcUseCase;
import com.study.management.domain.port.in.CroUseCase;
import com.study.management.domain.port.in.HospitalUseCase;
import com.study.management.domain.port.in.InvestigatorUseCase;
import com.study.management.domain.port.in.SubjectUseCase;
import com.study.management.infrastructure.web.dto.CognitoGroupInfo;
import com.study.management.infrastructure.web.dto.CognitoSyncReport;
import com.study.management.infrastructure.web.dto.RoleSyncSummary;
import com.study.management.infrastructure.web.dto.SyncDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GroupType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListGroupsRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListGroupsResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersInGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersInGroupResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ResourceNotFoundException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CognitoSyncService {

    private static final Logger log = LoggerFactory.getLogger(CognitoSyncService.class);

    private final CognitoIdentityProviderClient cognitoClient;
    private final String userPoolId;

    /** Cognito group name → role label used in sync report */
    private final Map<String, String> groupToRole;

    private final InvestigatorUseCase   investigatorUseCase;
    private final CoInvestigatorUseCase coInvestigatorUseCase;
    private final CrcUseCase            crcUseCase;
    private final CroUseCase            croUseCase;
    private final HospitalUseCase       hospitalUseCase;
    private final SubjectUseCase        subjectUseCase;

    public CognitoSyncService(
            CognitoIdentityProviderClient                           cognitoClient,
            @Value("${app.cognito.user-pool-id}")                  String userPoolId,
            @Value("${app.cognito.groups.primary-investigator}")    String piGroup,
            @Value("${app.cognito.groups.co-investigator}")         String ciGroup,
            @Value("${app.cognito.groups.crc}")                     String crcGroup,
            @Value("${app.cognito.groups.cro}")                     String croGroup,
            @Value("${app.cognito.groups.hospital}")                String hospitalGroup,
            @Value("${app.cognito.groups.subject}")                 String subjectGroup,
            InvestigatorUseCase   investigatorUseCase,
            CoInvestigatorUseCase coInvestigatorUseCase,
            CrcUseCase            crcUseCase,
            CroUseCase            croUseCase,
            HospitalUseCase       hospitalUseCase,
            SubjectUseCase        subjectUseCase) {

        this.cognitoClient = cognitoClient;
        this.userPoolId    = userPoolId;

        this.groupToRole = new LinkedHashMap<>();
        groupToRole.put(piGroup,       "primary-investigator");
        groupToRole.put(ciGroup,       "co-investigator");
        groupToRole.put(crcGroup,      "crc");
        groupToRole.put(croGroup,      "cro");
        groupToRole.put(hospitalGroup, "hospital");
        groupToRole.put(subjectGroup,  "subject");

        this.investigatorUseCase   = investigatorUseCase;
        this.coInvestigatorUseCase = coInvestigatorUseCase;
        this.crcUseCase            = crcUseCase;
        this.croUseCase            = croUseCase;
        this.hospitalUseCase       = hospitalUseCase;
        this.subjectUseCase        = subjectUseCase;
    }

    // ── Sync ────────────────────────────────────────────────────────────────

    public CognitoSyncReport sync() {
        List<SyncDetail>             details      = new ArrayList<>();
        Map<String, RoleSyncSummary> summary      = new LinkedHashMap<>();
        groupToRole.values().forEach(role -> summary.put(role, new RoleSyncSummary(0, 0, 0)));
        int totalScanned = 0;

        for (Map.Entry<String, String> entry : groupToRole.entrySet()) {
            String groupName = entry.getKey();
            String roleLabel = entry.getValue();

            log.info("Syncing Cognito group '{}' → role '{}'", groupName, roleLabel);

            List<UserType> groupUsers;
            try {
                groupUsers = fetchUsersInGroup(groupName);
            } catch (ResourceNotFoundException e) {
                log.error("Cognito group '{}' not found in user pool '{}'. " +
                          "Check app.cognito.groups.{} in application.properties",
                          groupName, userPoolId, roleLabel);
                // record every configured group as a single GROUP_NOT_FOUND error
                SyncDetail d = new SyncDetail();
                d.setUsername("-");
                d.setEmail("-");
                d.setRole(roleLabel);
                d.setAction("GROUP_NOT_FOUND");
                d.setMessage("Cognito group '" + groupName + "' does not exist. " +
                             "Call GET /api/v1/admin/sync/cognito/groups to see actual group names.");
                details.add(d);
                summary.get(roleLabel).setErrors(1);
                continue;
            }

            log.info("Found {} users in group '{}'", groupUsers.size(), groupName);
            totalScanned += groupUsers.size();

            Function<Map<String, String>, String> creator = resolveCreator(roleLabel);

            for (UserType user : groupUsers) {
                Map<String, String> attrs = toAttrMap(user);
                SyncDetail detail = new SyncDetail();
                detail.setUsername(user.username());
                detail.setEmail(attrs.get("email"));
                detail.setRole(roleLabel);

                try {
                    String entityId = creator.apply(attrs);
                    detail.setAction("CREATED");
                    detail.setEntityId(entityId);
                    summary.get(roleLabel).setCreated(summary.get(roleLabel).getCreated() + 1);
                    log.info("Created {} for Cognito user '{}'", roleLabel, user.username());

                } catch (IllegalArgumentException e) {
                    detail.setAction("SKIPPED");
                    detail.setMessage(e.getMessage());
                    summary.get(roleLabel).setSkipped(summary.get(roleLabel).getSkipped() + 1);
                    log.debug("Skipped Cognito user '{}': {}", user.username(), e.getMessage());

                } catch (Exception e) {
                    detail.setAction("ERROR");
                    detail.setMessage(e.getMessage());
                    summary.get(roleLabel).setErrors(summary.get(roleLabel).getErrors() + 1);
                    log.error("Error syncing Cognito user '{}': {}", user.username(), e.getMessage());
                }

                details.add(detail);
            }
        }

        CognitoSyncReport report = new CognitoSyncReport();
        report.setSyncedAt(OffsetDateTime.now());
        report.setTotalUsersScanned(totalScanned);
        report.setSummary(summary);
        report.setDetails(details);
        return report;
    }

    // ── Inline sync (called from list endpoints) ─────────────────────────────

    /**
     * Fetches all members of the Cognito group mapped to {@code roleLabel} and
     * creates DB records for any that are not already present (matched by email).
     * Silently skips duplicates; logs but does not throw on Cognito or DB errors
     * so that the calling list endpoint always returns the current DB state.
     */
    public void syncGroupIntoDb(String roleLabel) {
        String groupName = groupToRole.entrySet().stream()
                .filter(e -> roleLabel.equals(e.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (groupName == null) {
            log.warn("syncGroupIntoDb: no Cognito group configured for role '{}'", roleLabel);
            return;
        }

        List<UserType> users;
        try {
            users = fetchUsersInGroup(groupName);
        } catch (ResourceNotFoundException e) {
            log.warn("syncGroupIntoDb: Cognito group '{}' not found for role '{}'. " +
                     "Update app.cognito.groups.{} in application.properties.",
                     groupName, roleLabel, roleLabel);
            return;
        } catch (Exception e) {
            log.error("syncGroupIntoDb: failed to fetch group '{}': {}", groupName, e.getMessage());
            return;
        }

        log.debug("syncGroupIntoDb: {} users in Cognito group '{}', checking DB for gaps",
                  users.size(), groupName);

        Function<Map<String, String>, String> creator = resolveCreator(roleLabel);

        for (UserType user : users) {
            Map<String, String> attrs = toAttrMap(user);
            try {
                String entityId = creator.apply(attrs);
                log.info("syncGroupIntoDb: created {} entity {} for Cognito user '{}'",
                         roleLabel, entityId, user.username());
            } catch (IllegalArgumentException | DataIntegrityViolationException e) {
                // app-level or DB-level duplicate — already in table, nothing to do
                log.debug("syncGroupIntoDb: skipped '{}' (already exists): {}", user.username(), e.getMessage());
            } catch (Exception e) {
                log.error("syncGroupIntoDb: error creating {} for Cognito user '{}': {}",
                          roleLabel, user.username(), e.getMessage());
            }
        }
    }

    // ── Diagnostic: list all Cognito groups ─────────────────────────────────

    public List<CognitoGroupInfo> listGroupsForDiagnostic() {
        // fetch all actual groups from Cognito
        Set<String> actualGroupNames = fetchAllGroupNames();

        // build diagnostic entries for every configured group
        List<CognitoGroupInfo> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : groupToRole.entrySet()) {
            String groupName = entry.getKey();
            String roleLabel = entry.getValue();

            CognitoGroupInfo info = new CognitoGroupInfo();
            info.setCognitoGroupName(groupName);
            info.setConfiguredAs(roleLabel);
            info.setFoundInCognito(actualGroupNames.contains(groupName));

            if (info.isFoundInCognito()) {
                List<UserType> users = fetchUsersInGroup(groupName);
                info.setUserCount(users.size());
                info.setSampleEmails(users.stream()
                        .limit(5)
                        .map(u -> toAttrMap(u).getOrDefault("email", u.username()))
                        .collect(Collectors.toList()));
            } else {
                info.setUserCount(0);
                info.setSampleEmails(List.of());
            }
            result.add(info);
        }

        // also surface any Cognito groups that are NOT in our config (informational)
        Set<String> configuredNames = groupToRole.keySet();
        for (String actualName : actualGroupNames) {
            if (!configuredNames.contains(actualName)) {
                CognitoGroupInfo info = new CognitoGroupInfo();
                info.setCognitoGroupName(actualName);
                info.setConfiguredAs("(not configured)");
                info.setFoundInCognito(true);
                List<UserType> users = fetchUsersInGroup(actualName);
                info.setUserCount(users.size());
                info.setSampleEmails(users.stream()
                        .limit(3)
                        .map(u -> toAttrMap(u).getOrDefault("email", u.username()))
                        .collect(Collectors.toList()));
                result.add(info);
            }
        }

        return result;
    }

    // ── Cognito API calls ────────────────────────────────────────────────────

    /** Throws ResourceNotFoundException (unchecked) if the group does not exist. */
    private List<UserType> fetchUsersInGroup(String groupName) {
        List<UserType> users = new ArrayList<>();
        String nextToken = null;
        do {
            ListUsersInGroupRequest.Builder req = ListUsersInGroupRequest.builder()
                    .userPoolId(userPoolId)
                    .groupName(groupName)
                    .limit(60);
            if (nextToken != null) {
                req.nextToken(nextToken);
            }
            ListUsersInGroupResponse resp = cognitoClient.listUsersInGroup(req.build());
            users.addAll(resp.users());
            nextToken = resp.nextToken();
        } while (nextToken != null && !nextToken.isBlank());
        return users;
    }

    private Set<String> fetchAllGroupNames() {
        List<String> names = new ArrayList<>();
        String nextToken = null;
        do {
            ListGroupsRequest.Builder req = ListGroupsRequest.builder()
                    .userPoolId(userPoolId)
                    .limit(60);
            if (nextToken != null) {
                req.nextToken(nextToken);
            }
            ListGroupsResponse resp = cognitoClient.listGroups(req.build());
            resp.groups().stream().map(GroupType::groupName).forEach(names::add);
            nextToken = resp.nextToken();
        } while (nextToken != null && !nextToken.isBlank());
        return Set.copyOf(names);
    }

    // ── per-role entity creators ─────────────────────────────────────────────

    private Function<Map<String, String>, String> resolveCreator(String roleLabel) {
        return switch (roleLabel) {
            case "primary-investigator" -> this::syncPi;
            case "co-investigator"      -> this::syncCi;
            case "crc"                  -> this::syncCrc;
            case "cro"                  -> this::syncCro;
            case "hospital"             -> this::syncHospital;
            case "subject"              -> this::syncSubject;
            default -> throw new IllegalStateException("No creator for role: " + roleLabel);
        };
    }

    private String syncPi(Map<String, String> a) {
        PrimaryInvestigator pi = new PrimaryInvestigator();
        pi.setFirstName(a.get("given_name"));
        pi.setLastName(a.get("family_name"));
        pi.setEmail(resolveEmail(a));
        pi.setPhone(resolvePhone(a));
        return investigatorUseCase.createInvestigator(pi).getPiId().toString();
    }

    private String syncCi(Map<String, String> a) {
        CoInvestigator ci = new CoInvestigator();
        ci.setFirstName(a.get("given_name"));
        ci.setLastName(a.get("family_name"));
        ci.setEmail(resolveEmail(a));
        ci.setPhone(resolvePhone(a));
        return coInvestigatorUseCase.create(ci).getCiId().toString();
    }

    private String syncCrc(Map<String, String> a) {
        ClinicalResearchCoordinator crc = new ClinicalResearchCoordinator();
        crc.setFirstName(a.get("given_name"));
        crc.setLastName(a.get("family_name"));
        crc.setEmail(resolveEmail(a));
        crc.setPhone(resolvePhone(a));
        return crcUseCase.create(crc).getCrcId().toString();
    }

    private String syncCro(Map<String, String> a) {
        ContractResearchOrganization cro = new ContractResearchOrganization();
        String phone = resolvePhone(a);
        String orgName = a.getOrDefault("custom:organization",
                a.getOrDefault("name", phone != null ? phone : "Unknown"));
        cro.setOrganizationName(orgName);
        cro.setContactPerson(fullName(a));
        cro.setEmail(resolveEmail(a));
        cro.setPhone(phone);
        return croUseCase.create(cro).getCroId().toString();
    }

    private String syncHospital(Map<String, String> a) {
        Hospital h = new Hospital();
        String phone = resolvePhone(a);
        String orgName = a.getOrDefault("custom:organization",
                a.getOrDefault("name", phone != null ? phone : "Unknown"));
        h.setHospitalName(orgName);
        h.setContactPerson(fullName(a));
        h.setEmail(resolveEmail(a));
        h.setPhone(phone);
        return hospitalUseCase.create(h).getHospitalId().toString();
    }

    private String syncSubject(Map<String, String> a) {
        Subject s = new Subject();
        s.setFirstName(a.get("given_name"));
        s.setLastName(a.get("family_name"));
        s.setEmail(resolveEmail(a));
        s.setPhone(resolvePhone(a));
        String fn = a.getOrDefault("given_name", "");
        String ln = a.getOrDefault("family_name", "");
        if (!fn.isBlank() || !ln.isBlank()) {
            s.setInitials(
                (fn.isBlank() ? "" : fn.substring(0, 1).toUpperCase()) +
                (ln.isBlank() ? "" : ln.substring(0, 1).toUpperCase()));
        }
        return subjectUseCase.create(s).getSubjectId().toString();
    }

    // ── Single-role sync (returns a full report for one role) ────────────────

    public CognitoSyncReport syncSingleRole(String roleLabel) {
        List<SyncDetail>             details = new ArrayList<>();
        Map<String, RoleSyncSummary> summary = new LinkedHashMap<>();
        summary.put(roleLabel, new RoleSyncSummary(0, 0, 0));

        String groupName = groupToRole.entrySet().stream()
                .filter(e -> roleLabel.equals(e.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (groupName == null) {
            SyncDetail d = new SyncDetail();
            d.setAction("ERROR");
            d.setRole(roleLabel);
            d.setMessage("No Cognito group configured for role '" + roleLabel + "'");
            details.add(d);
            summary.get(roleLabel).setErrors(1);
            return buildReport(0, summary, details);
        }

        List<UserType> users;
        try {
            users = fetchUsersInGroup(groupName);
        } catch (ResourceNotFoundException e) {
            SyncDetail d = new SyncDetail();
            d.setAction("GROUP_NOT_FOUND");
            d.setRole(roleLabel);
            d.setMessage("Cognito group '" + groupName + "' does not exist. " +
                         "Update app.cognito.groups." + roleLabel + " in application.properties. " +
                         "Call GET /api/v1/admin/sync/cognito/groups to see actual group names.");
            details.add(d);
            summary.get(roleLabel).setErrors(1);
            return buildReport(0, summary, details);
        }

        Function<Map<String, String>, String> creator = resolveCreator(roleLabel);

        for (UserType user : users) {
            Map<String, String> attrs = toAttrMap(user);
            SyncDetail detail = new SyncDetail();
            detail.setUsername(user.username());
            detail.setEmail(attrs.get("email"));
            detail.setRole(roleLabel);

            try {
                String entityId = creator.apply(attrs);
                detail.setAction("CREATED");
                detail.setEntityId(entityId);
                summary.get(roleLabel).setCreated(summary.get(roleLabel).getCreated() + 1);

            } catch (IllegalArgumentException | DataIntegrityViolationException e) {
                detail.setAction("SKIPPED");
                detail.setMessage(e.getMessage());
                summary.get(roleLabel).setSkipped(summary.get(roleLabel).getSkipped() + 1);

            } catch (Exception e) {
                detail.setAction("ERROR");
                detail.setMessage(e.getClass().getSimpleName() + ": " + e.getMessage());
                summary.get(roleLabel).setErrors(summary.get(roleLabel).getErrors() + 1);
            }

            details.add(detail);
        }

        return buildReport(users.size(), summary, details);
    }

    private CognitoSyncReport buildReport(int scanned,
                                          Map<String, RoleSyncSummary> summary,
                                          List<SyncDetail> details) {
        CognitoSyncReport report = new CognitoSyncReport();
        report.setSyncedAt(OffsetDateTime.now());
        report.setTotalUsersScanned(scanned);
        report.setSummary(summary);
        report.setDetails(details);
        return report;
    }

    // ── utilities ────────────────────────────────────────────────────────────

    /**
     * Returns the email to store in the DB.
     * 1. Uses the Cognito `email` attribute (normalised to lowercase).
     * 2. Falls back to the Cognito `_username` if it looks like an email address.
     * 3. Returns null if neither source yields an email (caller must handle).
     */
    /**
     * Returns the phone to store in the DB.
     * 1. Uses the Cognito `phone_number` attribute (E.164 format).
     * 2. Falls back to `_username` when it starts with `+` (phone-number-as-username pool).
     * 3. Returns null if neither source yields a phone.
     */
    private String resolvePhone(Map<String, String> a) {
        String phone = a.get("phone_number");
        if (phone == null || phone.isBlank()) {
            String username = a.get("_username");
            if (username != null && username.startsWith("+")) {
                phone = username;
            }
        }
        return (phone != null && !phone.isBlank()) ? phone.trim() : null;
    }

    private String resolveEmail(Map<String, String> a) {
        String email = a.get("email");
        if (email == null || email.isBlank()) {
            // some Cognito pools use email as the username field directly
            String username = a.get("_username");
            if (username != null && username.contains("@")) {
                email = username;
            }
        }
        return (email != null && !email.isBlank()) ? email.toLowerCase().trim() : null;
    }

    private Map<String, String> toAttrMap(UserType user) {
        Map<String, String> map = new LinkedHashMap<>();
        // store the Cognito username under a reserved key so syncX methods can use it
        map.put("_username", user.username());
        for (AttributeType attr : user.attributes()) {
            map.put(attr.name(), attr.value());
        }
        return map;
    }

    private String fullName(Map<String, String> a) {
        String fn = a.getOrDefault("given_name", "");
        String ln = a.getOrDefault("family_name", "");
        String full = (fn + " " + ln).trim();
        return full.isBlank() ? a.getOrDefault("name", null) : full;
    }

    private String emailToOrgName(String email) {
        if (email == null) return null;
        int at = email.indexOf('@');
        return at > 0 ? email.substring(at + 1) : email;
    }
}
