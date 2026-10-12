package com.study.management.infrastructure.web;

import com.study.management.domain.model.StudyTeam;
import com.study.management.domain.model.StudyTeamMember;
import com.study.management.domain.model.TeamMemberRole;
import com.study.management.domain.port.in.StudyTeamUseCase;
import com.study.management.infrastructure.web.dto.CreateStudyTeamRequest;
import com.study.management.infrastructure.web.dto.GrantStudyCreationRequest;
import com.study.management.infrastructure.web.dto.UpdateStudyTeamRequest;
import com.study.management.infrastructure.web.dto.StudyCreationDelegateResponse;
import com.study.management.infrastructure.web.dto.StudyTeamCreateResponse;
import com.study.management.infrastructure.web.dto.StudyTeamMemberResponse;
import com.study.management.infrastructure.web.dto.StudyTeamResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Study Teams", description = "Create and manage clinical trial study teams")
@RestController
@RequestMapping("/api/v1/study-teams")
public class StudyTeamController {

    private final StudyTeamUseCase studyTeamUseCase;

    public StudyTeamController(StudyTeamUseCase studyTeamUseCase) {
        this.studyTeamUseCase = studyTeamUseCase;
    }

    @Operation(
        summary = "Create a study team",
        description = "Creates a new study team. PI is looked up by email. " +
                      "Returns a unique trialId as the permanent reference. " +
                      "Allowed roles: ADMIN, PRIMARY_INVESTIGATOR, CRO."
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CRO')")
    @PostMapping
    public ResponseEntity<StudyTeamCreateResponse> create(
            @Valid @RequestBody CreateStudyTeamRequest req,
            @AuthenticationPrincipal Jwt jwt) {

        String createdByUserId = resolveCallerId(jwt);
        String createdByRole = jwt.getClaimAsString("custom:role");

        StudyTeam team = new StudyTeam();
        team.setStudyName(req.getStudyName());
        team.setPrimaryInvestigatorEmail(req.getPrimaryInvestigatorEmail());
        team.setCreatedByUserId(createdByUserId);
        team.setCreatedByRole(createdByRole);

        addEmailMembers(team, req.getCoInvestigatorEmails(), TeamMemberRole.CO_INVESTIGATOR);
        addEmailMembers(team, req.getCrcEmails(),            TeamMemberRole.CRC);
        addEmailMembers(team, req.getCroEmails(),            TeamMemberRole.CRO);
        addEmailMembers(team, req.getHospitalEmails(),       TeamMemberRole.HOSPITAL);

        StudyTeam saved = studyTeamUseCase.createStudyTeam(team);
        return ResponseEntity.status(HttpStatus.CREATED).body(toCreateResponse(saved));
    }

    @Operation(
        summary = "My study teams",
        description = "Returns study teams where the authenticated user is a member. " +
                      "Works for all roles: PRIMARY_INVESTIGATOR, CO_INVESTIGATOR, CRC, CRO, HOSPITAL. " +
                      "Role and identity are resolved automatically from the JWT — no query parameters needed."
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC', 'CRO', 'HOSPITAL')")
    @GetMapping("/my-teams")
    public ResponseEntity<List<StudyTeamResponse>> myTeams(@AuthenticationPrincipal Jwt jwt) {
        String callerUserId = resolveCallerId(jwt);
        String callerRole = jwt.getClaimAsString("custom:role");
        List<StudyTeamResponse> teams = studyTeamUseCase.getMyTeams(callerUserId, callerRole).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(teams);
    }

    @Operation(
        summary = "Get study teams by PI mobile number",
        description = "Returns all study teams where the given mobile number belongs to the primary investigator. " +
                      "Accepts E.164 format (e.g. +919876543210). Restricted to ADMIN and PRIMARY_INVESTIGATOR roles."
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR')")
    @GetMapping("/by-pi")
    public ResponseEntity<List<StudyTeamResponse>> byPiPhone(
            @RequestParam("phone") String phone) {
        List<StudyTeamResponse> teams = studyTeamUseCase.getMyTeams(phone, "primary-investigator").stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(teams);
    }

    @Operation(
        summary = "Get study teams by member mobile number and role",
        description = "Returns all study teams in which the person/organisation identified by the given mobile number " +
                      "is a member with the specified role. " +
                      "Accepted role values: co-investigator, crc, cro, hospital. " +
                      "Accepts E.164 phone format (e.g. +919876543210)."
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC', 'CRO', 'HOSPITAL')")
    @GetMapping("/by-member")
    public ResponseEntity<List<StudyTeamResponse>> byMemberPhone(
            @RequestParam("phone") String phone,
            @RequestParam("role")  String role) {
        List<StudyTeamResponse> teams = studyTeamUseCase.getMyTeams(phone, role).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(teams);
    }

    @Operation(
        summary = "Update a study team",
        description = """
            Full or partial update of a study team. All fields are optional:
            - Omit `studyName` to leave the current name unchanged.
            - Omit `primaryInvestigatorEmail` to leave the PI unchanged.
            - For member lists (`coInvestigatorEmails`, `crcEmails`, `croEmails`, `hospitalEmails`):
              - **Omit** the field → role's members left unchanged.
              - **Send an empty list `[]`** → all members of that role are removed.
              - **Send a list of emails/phones** → full replacement: members not in the list are removed, new ones are added.

            Allowed roles: ADMIN, PRIMARY_INVESTIGATOR."""
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CRO')")
    @PutMapping("/{trialId}")
    public ResponseEntity<StudyTeamResponse> update(
            @PathVariable UUID trialId,
            @RequestBody UpdateStudyTeamRequest req) {

        StudyTeam updates = new StudyTeam();
        updates.setStudyName(req.getStudyName());
        updates.setPrimaryInvestigatorEmail(req.getPrimaryInvestigatorEmail());
        // Only add members for roles whose list was explicitly included in the request
        if (req.getCoInvestigatorEmails() != null)
            addEmailMembers(updates, req.getCoInvestigatorEmails(), TeamMemberRole.CO_INVESTIGATOR);
        if (req.getCrcEmails() != null)
            addEmailMembers(updates, req.getCrcEmails(),            TeamMemberRole.CRC);
        if (req.getCroEmails() != null)
            addEmailMembers(updates, req.getCroEmails(),            TeamMemberRole.CRO);
        if (req.getHospitalEmails() != null)
            addEmailMembers(updates, req.getHospitalEmails(),       TeamMemberRole.HOSPITAL);

        StudyTeam saved = studyTeamUseCase.updateStudyTeam(trialId, updates);
        return ResponseEntity.ok(toResponse(saved));
    }

    @Operation(summary = "Get study team by trial ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC', 'CRO', 'HOSPITAL')")
    @GetMapping("/{trialId}")
    public ResponseEntity<StudyTeamResponse> getById(@PathVariable UUID trialId) {
        return ResponseEntity.ok(toResponse(studyTeamUseCase.getByTrialId(trialId)));
    }

    // ── Study-creation delegation ──────────────────────────────────────────────

    @Operation(
        summary = "Grant study-creation privilege",
        description = "Primary investigator grants a CO_INVESTIGATOR or CRC on their team the right to create studies. " +
                      "Only the PI of the team may call this endpoint."
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR')")
    @PostMapping("/{trialId}/delegates")
    public ResponseEntity<StudyCreationDelegateResponse> grantStudyCreation(
            @PathVariable UUID trialId,
            @Valid @RequestBody GrantStudyCreationRequest req,
            @AuthenticationPrincipal Jwt jwt) {

        String callerUserId = resolveCallerId(jwt);

        StudyTeamMember member = studyTeamUseCase.grantStudyCreation(trialId, req.getMemberId(), callerUserId);
        return ResponseEntity.ok(toDelegateResponse(trialId, member));
    }

    @Operation(
        summary = "Revoke study-creation privilege",
        description = "Primary investigator removes the study-creation right previously granted to a team member."
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR')")
    @DeleteMapping("/{trialId}/delegates/{memberId}")
    public ResponseEntity<StudyCreationDelegateResponse> revokeStudyCreation(
            @PathVariable UUID trialId,
            @PathVariable UUID memberId,
            @AuthenticationPrincipal Jwt jwt) {

        String callerUserId = resolveCallerId(jwt);

        StudyTeamMember member = studyTeamUseCase.revokeStudyCreation(trialId, memberId, callerUserId);
        return ResponseEntity.ok(toDelegateResponse(trialId, member));
    }

    @Operation(
        summary = "List current study-creation delegates",
        description = "Returns all team members who currently have study-creation privilege on this team."
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC')")
    @GetMapping("/{trialId}/delegates")
    public ResponseEntity<List<StudyCreationDelegateResponse>> listDelegates(@PathVariable UUID trialId) {
        List<StudyCreationDelegateResponse> delegates = studyTeamUseCase.listDelegates(trialId).stream()
                .map(m -> toDelegateResponse(trialId, m))
                .collect(Collectors.toList());
        return ResponseEntity.ok(delegates);
    }

    // ── List all study teams ───────────────────────────────────────────────────

    @Operation(summary = "List all study teams")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC', 'CRO', 'HOSPITAL')")
    @GetMapping
    public ResponseEntity<List<StudyTeamResponse>> listAll() {
        List<StudyTeamResponse> teams = studyTeamUseCase.getAllTeams().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(teams);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String resolveCallerId(Jwt jwt) {
        String phone = jwt.getClaimAsString("phone_number");
        if (phone != null && !phone.isBlank()) return phone;
        String email = jwt.getClaimAsString("email");
        if (email != null && !email.isBlank()) return email;
        return jwt.getSubject();
    }

    private void addEmailMembers(StudyTeam team, List<String> emails, TeamMemberRole role) {
        if (emails == null) return;
        for (String email : emails) {
            StudyTeamMember member = new StudyTeamMember();
            member.setUserId(email);
            member.setEmail(email);
            member.setRole(role);
            member.setStudyTeam(team);
            team.getMembers().add(member);
        }
    }

    private StudyTeamCreateResponse toCreateResponse(StudyTeam team) {
        StudyTeamCreateResponse r = new StudyTeamCreateResponse();
        r.setTrialId(team.getTrialId());
        r.setMessage("Study team created successfully. Use trialId as the permanent reference for this record.");
        r.setStudyName(team.getStudyName());
        r.setCreatedByUserId(team.getCreatedByUserId());
        r.setCreatedByRole(team.getCreatedByRole());
        r.setCreatedAt(team.getCreatedAt());
        if (team.getPrimaryInvestigator() != null) {
            r.setPrimaryInvestigatorEmail(team.getPrimaryInvestigator().getEmail());
            r.setPrimaryInvestigatorName(
                    team.getPrimaryInvestigator().getFirstName() + " " +
                    team.getPrimaryInvestigator().getLastName());
        }
        List<StudyTeamMember> members = team.getMembers();
        r.setCoInvestigators(membersOf(members, TeamMemberRole.CO_INVESTIGATOR));
        r.setCrcs(membersOf(members, TeamMemberRole.CRC));
        r.setCros(membersOf(members, TeamMemberRole.CRO));
        r.setHospitals(membersOf(members, TeamMemberRole.HOSPITAL));
        return r;
    }

    private StudyTeamResponse toResponse(StudyTeam team) {
        StudyTeamResponse r = new StudyTeamResponse();
        r.setTrialId(team.getTrialId());
        r.setStudyName(team.getStudyName());
        r.setCreatedByUserId(team.getCreatedByUserId());
        r.setCreatedByRole(team.getCreatedByRole());
        r.setCreatedAt(team.getCreatedAt());
        r.setUpdatedAt(team.getUpdatedAt());
        if (team.getPrimaryInvestigator() != null) {
            r.setPrimaryInvestigatorEmail(team.getPrimaryInvestigator().getEmail());
            r.setPrimaryInvestigatorName(
                    team.getPrimaryInvestigator().getFirstName() + " " +
                    team.getPrimaryInvestigator().getLastName());
        }
        List<StudyTeamMember> members = team.getMembers();
        r.setCoInvestigators(membersOf(members, TeamMemberRole.CO_INVESTIGATOR));
        r.setCrcs(membersOf(members, TeamMemberRole.CRC));
        r.setCros(membersOf(members, TeamMemberRole.CRO));
        r.setHospitals(membersOf(members, TeamMemberRole.HOSPITAL));
        return r;
    }

    private StudyCreationDelegateResponse toDelegateResponse(UUID trialId, StudyTeamMember m) {
        StudyCreationDelegateResponse r = new StudyCreationDelegateResponse();
        r.setTrialId(trialId);
        r.setMemberId(m.getMemberId());
        r.setEmail(m.getEmail());
        r.setRole(m.getRole());
        r.setCanCreateStudy(m.isCanCreateStudy());
        r.setGrantedAt(m.getStudyCreationGrantedAt());
        r.setGrantedBy(m.getStudyCreationGrantedBy());
        if (m.getRole() == TeamMemberRole.CO_INVESTIGATOR && m.getCoInvestigator() != null) {
            r.setMemberName(m.getCoInvestigator().getFirstName() + " " + m.getCoInvestigator().getLastName());
        } else if (m.getRole() == TeamMemberRole.CRC && m.getCrc() != null) {
            r.setMemberName(m.getCrc().getFirstName() + " " + m.getCrc().getLastName());
        }
        return r;
    }

    private List<StudyTeamMemberResponse> membersOf(List<StudyTeamMember> all, TeamMemberRole role) {
        return all.stream()
                .filter(m -> m.getRole() == role)
                .map(m -> {
                    StudyTeamMemberResponse mr = new StudyTeamMemberResponse();
                    mr.setMemberId(m.getMemberId());
                    mr.setEmail(m.getEmail());
                    mr.setRole(m.getRole());
                    mr.setCreatedAt(m.getCreatedAt());
                    mr.setCanCreateStudy(m.isCanCreateStudy());
                    mr.setStudyCreationGrantedAt(m.getStudyCreationGrantedAt());
                    mr.setStudyCreationGrantedBy(m.getStudyCreationGrantedBy());
                    switch (role) {
                        case CO_INVESTIGATOR -> {
                            if (m.getCoInvestigator() != null) {
                                mr.setName(m.getCoInvestigator().getFirstName() + " " + m.getCoInvestigator().getLastName());
                                mr.setMemberNumber(m.getCoInvestigator().getCiNumber());
                            }
                        }
                        case CRC -> {
                            if (m.getCrc() != null) {
                                mr.setName(m.getCrc().getFirstName() + " " + m.getCrc().getLastName());
                                mr.setMemberNumber(m.getCrc().getCrcNumber());
                            }
                        }
                        case CRO -> {
                            if (m.getCro() != null) {
                                mr.setName(m.getCro().getOrganizationName());
                                mr.setMemberNumber(m.getCro().getCroNumber());
                            }
                        }
                        case HOSPITAL -> {
                            if (m.getHospital() != null) {
                                mr.setName(m.getHospital().getHospitalName());
                                mr.setMemberNumber(m.getHospital().getHospitalNumber());
                            }
                        }
                    }
                    return mr;
                })
                .collect(Collectors.toList());
    }
}