package com.study.management.domain.port.out;

import com.study.management.domain.model.PrimaryInvestigator;
import com.study.management.domain.model.StudyTeam;
import com.study.management.domain.model.StudyTeamMember;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudyTeamPort {
    StudyTeam save(StudyTeam team);
    Optional<StudyTeam> findById(UUID trialId);
    List<StudyTeam> findAll();
    List<StudyTeam> findByPi(PrimaryInvestigator pi);
    List<StudyTeam> findByCoInvestigatorUserId(String userId);
    List<StudyTeam> findByCrcUserId(String userId);
    List<StudyTeam> findByCroUserId(String userId);
    List<StudyTeam> findByHospitalUserId(String userId);
    StudyTeamMember saveTeamMember(StudyTeamMember member);
    void deleteTeamMember(UUID memberId);
}
