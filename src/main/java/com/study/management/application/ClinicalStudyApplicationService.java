package com.study.management.application;

import com.study.management.domain.model.ClinicalStudyStatus;
import com.study.management.domain.model.MedicalCenter;
import com.study.management.domain.model.PrimaryInvestigator;
import com.study.management.domain.model.StudyAccess;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.StudySiteAssignment;
import com.study.management.domain.model.UserRole;
import com.study.management.domain.model.exception.BusinessRuleException;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.domain.port.in.ClinicalStudyUseCase;
import com.study.management.domain.port.out.ClinicalStudyPort;
import com.study.management.domain.port.out.InvestigatorPort;
import com.study.management.domain.port.out.MedicalCenterPort;
import com.study.management.domain.port.out.StudyAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ClinicalStudyApplicationService implements ClinicalStudyUseCase {

    private final ClinicalStudyPort studyPort;
    private final InvestigatorPort investigatorPort;
    private final MedicalCenterPort centerPort;
    private final StudyAccessPort accessPort;

    public ClinicalStudyApplicationService(ClinicalStudyPort studyPort,
                                            InvestigatorPort investigatorPort,
                                            MedicalCenterPort centerPort,
                                            StudyAccessPort accessPort) {
        this.studyPort = studyPort;
        this.investigatorPort = investigatorPort;
        this.centerPort = centerPort;
        this.accessPort = accessPort;
    }

    @Override
    public StudyEntity createStudy(StudyEntity study) {
        if (study.getStudyNumber() != null && studyPort.existsByStudyNumber(study.getStudyNumber())) {
            throw new IllegalArgumentException("Study number '" + study.getStudyNumber() + "' already exists.");
        }
        if (study.getCreatedByPi() != null && study.getCreatedByPi().getPiId() != null) {
            PrimaryInvestigator pi = investigatorPort.findById(study.getCreatedByPi().getPiId())
                    .orElseThrow(() -> new ResourceNotFoundException("PrimaryInvestigator",
                            study.getCreatedByPi().getPiId()));
            study.setCreatedByPi(pi);
        }
        study.setStatus(ClinicalStudyStatus.INACTIVE);
        return studyPort.save(study);
    }

    @Override
    @Transactional(readOnly = true)
    public StudyEntity getById(UUID studyId) {
        return studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyEntity> getStudiesForPi(UUID piId, ClinicalStudyStatus status) {
        PrimaryInvestigator pi = investigatorPort.findById(piId)
                .orElseThrow(() -> new ResourceNotFoundException("PrimaryInvestigator", piId));
        if (status != null) {
            return studyPort.findByPiAndStatus(pi, status);
        }
        return studyPort.findByPi(pi);
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
        }
        return studyPort.save(study);
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
