package com.study.management.domain.port.in;

import com.study.management.domain.model.StudyTeam;
import com.study.management.domain.model.StudyTeamMember;

import java.util.List;
import java.util.UUID;

public interface StudyTeamUseCase {
    StudyTeam createStudyTeam(StudyTeam team);
    StudyTeam updateStudyTeam(UUID trialId, StudyTeam updates);
    StudyTeam getByTrialId(UUID trialId);
    List<StudyTeam> getAllTeams();
    List<StudyTeam> getMyTeams(String callerUserId, String callerRole);

    StudyTeamMember grantStudyCreation(UUID trialId, UUID memberId, String grantedByUserId);
    StudyTeamMember revokeStudyCreation(UUID trialId, UUID memberId, String revokedByUserId);
    List<StudyTeamMember> listDelegates(UUID trialId);
}
