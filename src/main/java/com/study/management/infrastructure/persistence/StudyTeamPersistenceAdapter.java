package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.PrimaryInvestigator;
import com.study.management.domain.model.StudyTeam;
import com.study.management.domain.model.StudyTeamMember;
import com.study.management.domain.port.out.StudyTeamPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class StudyTeamPersistenceAdapter implements StudyTeamPort {

    private final JpaStudyTeamRepository jpa;
    private final JpaStudyTeamMemberRepository memberJpa;

    public StudyTeamPersistenceAdapter(JpaStudyTeamRepository jpa,
                                       JpaStudyTeamMemberRepository memberJpa) {
        this.jpa = jpa;
        this.memberJpa = memberJpa;
    }

    @Override public StudyTeam save(StudyTeam team)                        { return jpa.save(team); }
    @Override public Optional<StudyTeam> findById(UUID trialId)            { return jpa.findById(trialId); }
    @Override public List<StudyTeam> findAll()                             { return jpa.findAll(); }
    @Override public List<StudyTeam> findByPi(PrimaryInvestigator pi)             { return jpa.findByPrimaryInvestigator(pi); }
    @Override public List<StudyTeam> findByCoInvestigatorUserId(String userId)   { return jpa.findTeamsForCoInvestigator(userId); }
    @Override public List<StudyTeam> findByCrcUserId(String userId)               { return jpa.findTeamsForCrc(userId); }
    @Override public List<StudyTeam> findByCroUserId(String userId)               { return jpa.findTeamsForCro(userId); }
    @Override public List<StudyTeam> findByHospitalUserId(String userId)          { return jpa.findTeamsForHospital(userId); }
    @Override public StudyTeamMember saveTeamMember(StudyTeamMember m)            { return memberJpa.save(m); }
    @Override public void deleteTeamMember(UUID memberId)                         { memberJpa.deleteById(memberId); }
}
