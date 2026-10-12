package com.study.management.domain.port.out;

import com.study.management.domain.model.Study;
import com.study.management.domain.model.StudyPatient;

import java.util.List;
import java.util.Optional;

public interface StudyPatientPort {

    StudyPatient save(StudyPatient patient);

    List<StudyPatient> findByStudy(Study study);

    Optional<StudyPatient> findByStudyAndPatientNumber(Study study, String patientNumber);

    int countByStudy(Study study);
}
