package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.DocumentType;
import com.study.management.domain.model.StudyDocument;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.port.out.StudyDocumentPort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StudyDocumentPersistenceAdapter implements StudyDocumentPort {

    private final JpaStudyDocumentRepository jpa;

    public StudyDocumentPersistenceAdapter(JpaStudyDocumentRepository jpa) {
        this.jpa = jpa;
    }

    @Override public StudyDocument save(StudyDocument d)                           { return jpa.save(d); }
    @Override public List<StudyDocument> findByStudy(StudyEntity s)                { return jpa.findByStudy(s); }
    @Override public List<StudyDocument> findByStudyAndType(StudyEntity s, DocumentType t) {
        return jpa.findByStudyAndDocumentType(s, t);
    }
    @Override public List<StudyDocument> findByStudyAndCurrent(StudyEntity s, boolean c) {
        return jpa.findByStudyAndIsCurrent(s, c);
    }
    @Override public List<StudyDocument> findByStudyAndTypeAndCurrent(StudyEntity s, DocumentType t, boolean c) {
        return jpa.findByStudyAndDocumentTypeAndIsCurrent(s, t, c);
    }
}
