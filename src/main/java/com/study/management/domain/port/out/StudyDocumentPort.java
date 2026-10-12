package com.study.management.domain.port.out;

import com.study.management.domain.model.DocumentType;
import com.study.management.domain.model.StudyDocument;
import com.study.management.domain.model.StudyEntity;

import java.util.List;

public interface StudyDocumentPort {
    StudyDocument save(StudyDocument document);
    List<StudyDocument> findByStudy(StudyEntity study);
    List<StudyDocument> findByStudyAndType(StudyEntity study, DocumentType type);
    List<StudyDocument> findByStudyAndCurrent(StudyEntity study, boolean current);
    List<StudyDocument> findByStudyAndTypeAndCurrent(StudyEntity study, DocumentType type, boolean current);
}
