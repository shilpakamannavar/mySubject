package com.study.management.application;

import com.study.management.domain.model.ClinicalResearchCoordinator;
import com.study.management.domain.model.CoInvestigator;
import com.study.management.domain.model.ContractResearchOrganization;
import com.study.management.domain.model.Hospital;
import com.study.management.domain.model.PrimaryInvestigator;
import com.study.management.domain.model.StudyTeam;
import com.study.management.domain.model.StudyTeamMember;
import com.study.management.domain.model.TeamMemberRole;
import com.study.management.domain.model.exception.BusinessRuleException;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.domain.port.in.StudyTeamUseCase;
import com.study.management.domain.port.out.CoInvestigatorPort;
import com.study.management.domain.port.out.CrcPort;
import com.study.management.domain.port.out.CroPort;
import com.study.management.domain.port.out.HospitalPort;
import com.study.management.domain.port.out.InvestigatorPort;
import com.study.management.domain.port.out.StudyTeamPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class StudyTeamApplicationService implements StudyTeamUseCase {

    private final StudyTeamPort        studyTeamPort;
    private final InvestigatorPort     investigatorPort;
    private final CoInvestigatorPort   coInvestigatorPort;
    private final CrcPort              crcPort;
    private final CroPort              croPort;
    private final HospitalPort         hospitalPort;

    public StudyTeamApplicationService(StudyTeamPort studyTeamPort,
                                        InvestigatorPort investigatorPort,
                                        CoInvestigatorPort coInvestigatorPort,
                                        CrcPort crcPort,
                                        CroPort croPort,
                                        HospitalPort hospitalPort) {
        this.studyTeamPort      = studyTeamPort;
        this.investigatorPort   = investigatorPort;
        this.coInvestigatorPort = coInvestigatorPort;
        this.crcPort            = crcPort;
        this.croPort            = croPort;
        this.hospitalPort       = hospitalPort;
    }

    @Override
    public StudyTeam createStudyTeam(StudyTeam team) {
        String piEmail = team.getPrimaryInvestigatorEmail();
        if (piEmail == null || piEmail.isBlank()) {
            throw new IllegalArgumentException("primaryInvestigatorEmail is required.");
        }

        PrimaryInvestigator pi = investigatorPort.findByPhone(piEmail)
                .or(() -> investigatorPort.findByEmail(piEmail))
                .orElseThrow(() -> new IllegalArgumentException(
                        "No PrimaryInvestigator found with identifier: " + piEmail));

        team.setPrimaryInvestigator(pi);

        for (StudyTeamMember member : team.getMembers()) {
            member.setStudyTeam(team);
            String id = member.getEmail();
            switch (member.getRole()) {
                case CO_INVESTIGATOR -> {
                    CoInvestigator ci = coInvestigatorPort.findByPhone(id)
                            .or(() -> coInvestigatorPort.findByEmail(id))
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "No CoInvestigator found with identifier: " + id));
                    member.setCoInvestigator(ci);
                }
                case CRC -> {
                    ClinicalResearchCoordinator crc = crcPort.findByPhone(id)
                            .or(() -> crcPort.findByEmail(id))
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "No CRC found with identifier: " + id));
                    member.setCrc(crc);
                }
                case CRO -> {
                    ContractResearchOrganization cro = croPort.findByPhone(id)
                            .or(() -> croPort.findByEmail(id))
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "No CRO found with identifier: " + id));
                    member.setCro(cro);
                }
                case HOSPITAL -> {
                    Hospital hospital = hospitalPort.findByPhone(id)
                            .or(() -> hospitalPort.findByEmail(id))
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "No Hospital found with identifier: " + id));
                    member.setHospital(hospital);
                }
            }
        }

        return studyTeamPort.save(team);
    }

    @Override
    public StudyTeam updateStudyTeam(UUID trialId, StudyTeam updates) {
        StudyTeam existing = getByTrialId(trialId);

        // Update study name if provided
        if (updates.getStudyName() != null && !updates.getStudyName().isBlank()) {
            existing.setStudyName(updates.getStudyName());
        }

        // Update PI if a new email/phone is provided
        if (updates.getPrimaryInvestigatorEmail() != null && !updates.getPrimaryInvestigatorEmail().isBlank()) {
            String piId = updates.getPrimaryInvestigatorEmail();
            PrimaryInvestigator pi = investigatorPort.findByPhone(piId)
                    .or(() -> investigatorPort.findByEmail(piId))
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No PrimaryInvestigator found with identifier: " + piId));
            existing.setPrimaryInvestigator(pi);
        }

        // For each role in the update, perform a full replace:
        // remove members no longer in the list, add new ones.
        for (TeamMemberRole role : new TeamMemberRole[]{
                TeamMemberRole.CO_INVESTIGATOR, TeamMemberRole.CRC,
                TeamMemberRole.CRO, TeamMemberRole.HOSPITAL}) {

            List<StudyTeamMember> incoming = updates.getMembers().stream()
                    .filter(m -> m.getRole() == role)
                    .toList();

            if (incoming.isEmpty() && updates.getMembers().stream().noneMatch(m -> m.getRole() == role)) {
                continue; // role not mentioned in the update — leave untouched
            }

            // Emails in the incoming list
            java.util.Set<String> incomingEmails = incoming.stream()
                    .map(StudyTeamMember::getEmail)
                    .collect(java.util.stream.Collectors.toSet());

            // Remove existing members for this role that are no longer in the list
            existing.getMembers().stream()
                    .filter(m -> m.getRole() == role && !incomingEmails.contains(m.getEmail()))
                    .map(StudyTeamMember::getMemberId)
                    .toList()
                    .forEach(id -> {
                        studyTeamPort.deleteTeamMember(id);
                        existing.getMembers().removeIf(m -> m.getMemberId().equals(id));
                    });

            // Existing emails — skip duplicates
            java.util.Set<String> existingEmails = existing.getMembers().stream()
                    .filter(m -> m.getRole() == role)
                    .map(StudyTeamMember::getEmail)
                    .collect(java.util.stream.Collectors.toSet());

            // Add new members not already on the team
            for (StudyTeamMember newMember : incoming) {
                if (existingEmails.contains(newMember.getEmail())) continue;
                String id = newMember.getEmail();
                switch (role) {
                    case CO_INVESTIGATOR -> {
                        CoInvestigator ci = coInvestigatorPort.findByPhone(id)
                                .or(() -> coInvestigatorPort.findByEmail(id))
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "No CoInvestigator found with identifier: " + id));
                        newMember.setCoInvestigator(ci);
                    }
                    case CRC -> {
                        ClinicalResearchCoordinator crc = crcPort.findByPhone(id)
                                .or(() -> crcPort.findByEmail(id))
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "No CRC found with identifier: " + id));
                        newMember.setCrc(crc);
                    }
                    case CRO -> {
                        ContractResearchOrganization cro = croPort.findByPhone(id)
                                .or(() -> croPort.findByEmail(id))
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "No CRO found with identifier: " + id));
                        newMember.setCro(cro);
                    }
                    case HOSPITAL -> {
                        Hospital hospital = hospitalPort.findByPhone(id)
                                .or(() -> hospitalPort.findByEmail(id))
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "No Hospital found with identifier: " + id));
                        newMember.setHospital(hospital);
                    }
                }
                newMember.setStudyTeam(existing);
                existing.getMembers().add(newMember);
            }
        }

        return studyTeamPort.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public StudyTeam getByTrialId(UUID trialId) {
        return studyTeamPort.findById(trialId)
                .orElseThrow(() -> new ResourceNotFoundException("StudyTeam", trialId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyTeam> getAllTeams() {
        return studyTeamPort.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyTeam> getMyTeams(String callerUserId, String callerRole) {
        if (callerRole == null) callerRole = "";
        callerRole = callerRole.toLowerCase().replace('_', '-');
        return switch (callerRole) {
            case "primary-investigator" -> {
                PrimaryInvestigator pi = investigatorPort.findByPhone(callerUserId)
                        .or(() -> investigatorPort.findByEmail(callerUserId))
                        .orElseThrow(() -> new ResourceNotFoundException("PrimaryInvestigator", callerUserId));
                yield studyTeamPort.findByPi(pi);
            }
            case "co-investigator" -> studyTeamPort.findByCoInvestigatorUserId(callerUserId);
            case "crc"             -> studyTeamPort.findByCrcUserId(callerUserId);
            case "cro"             -> studyTeamPort.findByCroUserId(callerUserId);
            case "hospital"        -> studyTeamPort.findByHospitalUserId(callerUserId);
            default -> throw new BusinessRuleException(
                    "Role '" + callerRole + "' does not have study team membership.");
        };
    }

    @Override
    public StudyTeamMember grantStudyCreation(UUID trialId, UUID memberId, String grantedByUserId) {
        StudyTeam team = getByTrialId(trialId);
        verifyCallerIsPi(team, grantedByUserId);

        StudyTeamMember member = resolveMember(team, memberId);
        if (member.getRole() != TeamMemberRole.CO_INVESTIGATOR && member.getRole() != TeamMemberRole.CRC) {
            throw new BusinessRuleException("Study creation privilege can only be granted to CO_INVESTIGATOR or CRC members.");
        }

        member.setCanCreateStudy(true);
        member.setStudyCreationGrantedAt(OffsetDateTime.now());
        member.setStudyCreationGrantedBy(grantedByUserId);
        return studyTeamPort.saveTeamMember(member);
    }

    @Override
    public StudyTeamMember revokeStudyCreation(UUID trialId, UUID memberId, String revokedByUserId) {
        StudyTeam team = getByTrialId(trialId);
        verifyCallerIsPi(team, revokedByUserId);

        StudyTeamMember member = resolveMember(team, memberId);
        member.setCanCreateStudy(false);
        member.setStudyCreationGrantedAt(null);
        member.setStudyCreationGrantedBy(null);
        return studyTeamPort.saveTeamMember(member);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyTeamMember> listDelegates(UUID trialId) {
        StudyTeam team = getByTrialId(trialId);
        return team.getMembers().stream()
                .filter(StudyTeamMember::isCanCreateStudy)
                .toList();
    }

    private void verifyCallerIsPi(StudyTeam team, String callerUserId) {
        PrimaryInvestigator pi = team.getPrimaryInvestigator();
        boolean isTeamPi = (pi.getPhone() != null && pi.getPhone().equals(callerUserId))
                || (pi.getEmail() != null && pi.getEmail().equals(callerUserId));
        if (!isTeamPi) {
            throw new BusinessRuleException("Only the primary investigator of this team may manage study-creation privileges.");
        }
    }

    private StudyTeamMember resolveMember(StudyTeam team, UUID memberId) {
        return team.getMembers().stream()
                .filter(m -> m.getMemberId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("StudyTeamMember", memberId));
    }
}