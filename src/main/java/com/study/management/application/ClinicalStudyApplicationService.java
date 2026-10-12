package com.study.management.application;

import com.study.management.domain.model.ClinicalResearchCoordinator;
import com.study.management.domain.model.ClinicalStudyStatus;
import com.study.management.domain.model.CoInvestigator;
import com.study.management.domain.model.MedicalCenter;
import com.study.management.domain.model.PrimaryInvestigator;
import com.study.management.domain.model.StudyAccess;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.StudySiteAssignment;
import com.study.management.domain.model.StudyTeam;
import com.study.management.domain.model.UserRole;
import com.study.management.domain.model.exception.BusinessRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.domain.port.in.ClinicalStudyUseCase;
import com.study.management.domain.model.StudyTeamMember;
import com.study.management.domain.model.TeamMemberRole;
import com.study.management.domain.port.out.ClinicalStudyPort;
import com.study.management.domain.port.out.CoInvestigatorPort;
import com.study.management.domain.port.out.CrcPort;
import com.study.management.domain.port.out.InvestigatorPort;
import com.study.management.domain.port.out.MedicalCenterPort;
import com.study.management.domain.port.out.PatientStudyLogPort;
import com.study.management.domain.port.out.StudyAccessPort;
import com.study.management.domain.port.out.StudyTeamPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class ClinicalStudyApplicationService implements ClinicalStudyUseCase {

    private static final Logger log = LoggerFactory.getLogger(ClinicalStudyApplicationService.class);

    private final ClinicalStudyPort studyPort;
    private final InvestigatorPort investigatorPort;
    private final CoInvestigatorPort coInvestigatorPort;
    private final CrcPort crcPort;
    private final MedicalCenterPort centerPort;
    private final StudyAccessPort accessPort;
    private final PatientStudyLogPort patientStudyLogPort;
    private final StudyTeamPort studyTeamPort;

    public ClinicalStudyApplicationService(ClinicalStudyPort studyPort,
                                            InvestigatorPort investigatorPort,
                                            CoInvestigatorPort coInvestigatorPort,
                                            CrcPort crcPort,
                                            MedicalCenterPort centerPort,
                                            StudyAccessPort accessPort,
                                            PatientStudyLogPort patientStudyLogPort,
                                            StudyTeamPort studyTeamPort) {
        this.studyPort = studyPort;
        this.investigatorPort = investigatorPort;
        this.coInvestigatorPort = coInvestigatorPort;
        this.crcPort = crcPort;
        this.centerPort = centerPort;
        this.accessPort = accessPort;
        this.patientStudyLogPort = patientStudyLogPort;
        this.studyTeamPort = studyTeamPort;
    }

    @Override
    public StudyEntity createStudy(StudyEntity study, String callerUserId, String callerRole) {
        if (study.getStudyName() == null || study.getStudyName().isBlank()) {
            throw new IllegalArgumentException("Study name is required.");
        }
        if (studyPort.existsByStudyName(study.getStudyName())) {
            throw new IllegalArgumentException("A study named '" + study.getStudyName() + "' already exists.");
        }
        if (study.getStudyNumber() == null || study.getStudyNumber().isBlank()) {
            String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String suffix   = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
            study.setStudyNumber("STU-" + datePart + "-" + suffix);
        } else if (studyPort.existsByStudyNumber(study.getStudyNumber())) {
            throw new IllegalArgumentException("Study number '" + study.getStudyNumber() + "' already exists.");
        }
        if (study.getStudyTeam() == null || study.getStudyTeam().getTrialId() == null) {
            throw new IllegalArgumentException("studyTeamId is required.");
        }
        StudyTeam team = studyTeamPort.findById(study.getStudyTeam().getTrialId())
                .orElseThrow(() -> new ResourceNotFoundException("StudyTeam",
                        study.getStudyTeam().getTrialId()));

        if ("co-investigator".equalsIgnoreCase(callerRole)) {
            assertDelegated(team, callerUserId, TeamMemberRole.CO_INVESTIGATOR);
        } else if ("crc".equalsIgnoreCase(callerRole)) {
            assertDelegated(team, callerUserId, TeamMemberRole.CRC);
        }

        study.setStudyTeam(team);
        study.setCreatedByPi(team.getPrimaryInvestigator());
        study.setStatus(ClinicalStudyStatus.INACTIVE);
        return studyPort.save(study);
    }

    private void assertDelegated(StudyTeam team, String callerUserId, TeamMemberRole expectedRole) {
        boolean authorized = team.getMembers().stream()
                .filter(m -> m.getRole() == expectedRole && m.isCanCreateStudy())
                .anyMatch(m -> {
                    if (expectedRole == TeamMemberRole.CO_INVESTIGATOR && m.getCoInvestigator() != null) {
                        CoInvestigator ci = m.getCoInvestigator();
                        return callerUserId.equals(ci.getPhone()) || callerUserId.equals(ci.getEmail());
                    }
                    if (expectedRole == TeamMemberRole.CRC && m.getCrc() != null) {
                        ClinicalResearchCoordinator crc = m.getCrc();
                        return callerUserId.equals(crc.getPhone()) || callerUserId.equals(crc.getEmail());
                    }
                    return false;
                });
        if (!authorized) {
            throw new BusinessRuleException(
                    "You do not have study-creation privilege on this team. Ask the primary investigator to grant it.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public StudyEntity getById(UUID studyId) {
        return studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyEntity> getAllStudies() {
        return studyPort.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyEntity> getStudiesForPiByPhone(String phone) {
        PrimaryInvestigator pi = investigatorPort.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("PrimaryInvestigator", phone));
        return mergedStudiesForPi(pi, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyEntity> getStudiesForPi(UUID piId, ClinicalStudyStatus status) {
        PrimaryInvestigator pi = investigatorPort.findById(piId)
                .orElseThrow(() -> new ResourceNotFoundException("PrimaryInvestigator", piId));
        return mergedStudiesForPi(pi, status);
    }

    /**
     * Returns all studies for a PI from both sources, deduped by studyId:
     * 1. Studies created directly by this PI (created_by_pi FK)
     * 2. Studies linked to study teams where this PI is the team PI
     */
    private List<StudyEntity> mergedStudiesForPi(PrimaryInvestigator pi, ClinicalStudyStatus status) {
        List<StudyEntity> direct = status != null
                ? studyPort.findByPiAndStatus(pi, status)
                : studyPort.findByPi(pi);

        List<UUID> teamIds = studyTeamPort.findByPi(pi).stream()
                .map(StudyTeam::getTrialId)
                .collect(Collectors.toList());
        List<StudyEntity> viaTeam = teamIds.isEmpty()
                ? List.of()
                : studyPort.findByTeamIds(teamIds);

        // Merge deduped by studyId, preserving direct results first
        LinkedHashMap<UUID, StudyEntity> merged = new LinkedHashMap<>();
        Stream.concat(direct.stream(), viaTeam.stream())
                .forEach(s -> merged.putIfAbsent(s.getStudyId(), s));

        List<StudyEntity> result = new ArrayList<>(merged.values());
        if (status != null) result.removeIf(s -> s.getStatus() != status);
        return result;
    }

    @Override
    public StudyEntity updateStatus(UUID studyId, ClinicalStudyStatus status, String reason) {
        StudyEntity study = getById(studyId);
        study.setStatus(status);
        if (status == ClinicalStudyStatus.DISABLED) {
            study.setDisabledAt(OffsetDateTime.now());
            study.setDisabledReason(reason);
            // Revoke all active study_access except PRIMARY_INVESTIGATOR
            List<StudyAccess> accessList = accessPort.findByStudyAndActiveExcludingRole(
                    study, true, UserRole.PRIMARY_INVESTIGATOR);
            for (StudyAccess access : accessList) {
                access.setIsActive(false);
                access.setRevokedAt(OffsetDateTime.now());
                accessPort.save(access);
            }
            patientStudyLogPort.lockReadOnlyByStudy(study);
        }
        return studyPort.save(study);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyEntity> getDesignStudies(String callerPhone, String callerRole) {
        if (callerRole == null) callerRole = "";
        callerRole = callerRole.toLowerCase().replace('_', '-');
        return switch (callerRole) {
            case "admin"                  -> studyPort.findAll();
            case "primary-investigator"   -> {
                PrimaryInvestigator pi = investigatorPort.findByPhone(callerPhone)
                        .or(() -> investigatorPort.findByEmail(callerPhone))
                        .orElseThrow(() -> new ResourceNotFoundException("PrimaryInvestigator", callerPhone));
                yield mergedStudiesForPi(pi, null);
            }
            case "co-investigator" -> studyPort.findByTeamIds(teamIdsFor(studyTeamPort.findByCoInvestigatorUserId(callerPhone)));
            case "crc"             -> studyPort.findByTeamIds(teamIdsFor(studyTeamPort.findByCrcUserId(callerPhone)));
            case "cro"             -> studyPort.findByTeamIds(teamIdsFor(studyTeamPort.findByCroUserId(callerPhone)));
            case "hospital"        -> studyPort.findByTeamIds(teamIdsFor(studyTeamPort.findByHospitalUserId(callerPhone)));
            default -> throw new BusinessRuleException("Role '" + callerRole + "' is not permitted to access Design the Study.");
        };
    }

    @Override
    public StudyEntity updateStudyDesign(UUID studyId, StudyEntity updates, String callerPhone, String callerRole) {
        StudyEntity study = getById(studyId);
        if (study.getStatus() == ClinicalStudyStatus.DISABLED) {
            throw new BusinessRuleException("Cannot edit a disabled study.");
        }
        assertCanEdit(study, callerPhone, callerRole);
        if (updates.getStudyName()                != null) study.setStudyName(updates.getStudyName());
        if (updates.getTitle()                    != null) study.setTitle(updates.getTitle());
        if (updates.getPhase()                    != null) study.setPhase(updates.getPhase());
        if (updates.getTherapeuticArea()          != null) study.setTherapeuticArea(updates.getTherapeuticArea());
        if (updates.getNoOfPatientsToEnroll()     != null) study.setNoOfPatientsToEnroll(updates.getNoOfPatientsToEnroll());
        if (updates.getTotalNumberOfVisits()      != null) study.setTotalNumberOfVisits(updates.getTotalNumberOfVisits());
        if (updates.getDateFirstPatientLastVisit()   != null) study.setDateFirstPatientLastVisit(updates.getDateFirstPatientLastVisit());
        if (updates.getDateLastPatientFirstVisit()   != null) study.setDateLastPatientFirstVisit(updates.getDateLastPatientFirstVisit());
        return studyPort.save(study);
    }

    private void assertCanEdit(StudyEntity study, String callerPhone, String callerRole) {
        if (callerRole == null) callerRole = "";
        callerRole = callerRole.toLowerCase().replace('_', '-');
        switch (callerRole) {
            case "admin" -> { /* always allowed */ }
            case "primary-investigator" -> {
                PrimaryInvestigator pi = study.getCreatedByPi();
                log.warn("[EDIT-CHECK] studyId={} callerPhone='{}' callerRole='{}' pi.phone='{}' pi.email='{}'",
                        study.getStudyId(), callerPhone, callerRole,
                        pi != null ? pi.getPhone() : "NULL-PI",
                        pi != null ? pi.getEmail()  : "NULL-PI");
                boolean isPi = phoneMatches(pi != null ? pi.getPhone() : null, callerPhone)
                            || phoneMatches(pi != null ? pi.getEmail()  : null, callerPhone);
                if (!isPi) throw new BusinessRuleException("Only the primary investigator of this study may edit it.");
            }
            case "co-investigator", "crc" -> {
                TeamMemberRole memberRole = "co-investigator".equals(callerRole.toLowerCase())
                        ? TeamMemberRole.CO_INVESTIGATOR : TeamMemberRole.CRC;
                boolean granted = study.getStudyTeam().getMembers().stream()
                        .filter(m -> m.getRole() == memberRole && m.isCanCreateStudy())
                        .anyMatch(m -> {
                            if (memberRole == TeamMemberRole.CO_INVESTIGATOR && m.getCoInvestigator() != null) {
                                CoInvestigator ci = m.getCoInvestigator();
                                return phoneMatches(ci.getPhone(), callerPhone) || phoneMatches(ci.getEmail(), callerPhone);
                            }
                            if (memberRole == TeamMemberRole.CRC && m.getCrc() != null) {
                                ClinicalResearchCoordinator crc = m.getCrc();
                                return phoneMatches(crc.getPhone(), callerPhone) || phoneMatches(crc.getEmail(), callerPhone);
                            }
                            return false;
                        });
                if (!granted) throw new BusinessRuleException(
                        "You do not have edit privilege on this study. Ask the primary investigator to grant it.");
            }
            default -> throw new BusinessRuleException("Role '" + callerRole + "' cannot edit a study.");
        }
    }

    /** Compares two phone/email identifiers after stripping all non-digit chars (handles +91 vs 91 vs raw 10-digit). */
    private boolean phoneMatches(String stored, String caller) {
        if (stored == null || caller == null || caller.isBlank()) return false;
        if (stored.equals(caller)) return true;
        String s = stored.replaceAll("\\D", "");
        String c = caller.replaceAll("\\D", "");
        if (s.isEmpty() || c.isEmpty()) return false;
        // Match on trailing 10 digits to handle country-code variations
        int len = Math.min(10, Math.min(s.length(), c.length()));
        return s.substring(s.length() - len).equals(c.substring(c.length() - len));
    }

    private List<UUID> teamIdsFor(List<StudyTeam> teams) {
        return teams.stream().map(StudyTeam::getTrialId).collect(Collectors.toList());
    }

    @Override
    public StudySiteAssignment assignSite(UUID studyId, StudySiteAssignment assignment) {
        StudyEntity study = getById(studyId);
        MedicalCenter center = centerPort.findById(assignment.getMedicalCenter().getCenterId())
                .orElseThrow(() -> new ResourceNotFoundException("MedicalCenter",
                        assignment.getMedicalCenter().getCenterId()));
        PrimaryInvestigator pi = null;
        if (assignment.getPrimaryInvestigator() != null && assignment.getPrimaryInvestigator().getPiId() != null) {
            pi = investigatorPort.findById(assignment.getPrimaryInvestigator().getPiId())
                    .orElseThrow(() -> new ResourceNotFoundException("PrimaryInvestigator",
                            assignment.getPrimaryInvestigator().getPiId()));
        }
        assignment.setStudy(study);
        assignment.setMedicalCenter(center);
        assignment.setPrimaryInvestigator(pi);
        return studyPort.saveSiteAssignment(assignment);
    }
}
