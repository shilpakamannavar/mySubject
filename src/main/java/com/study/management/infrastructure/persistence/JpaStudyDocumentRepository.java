package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.DocumentType;
import com.study.management.domain.model.StudyDocument;
import com.study.management.domain.model.StudyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaStudyDocumentRepository extends JpaRepository<StudyDocument, UUID> {
    List<StudyDocument> findByStudy(StudyEntity study);
    List<StudyDocument> findByStudyAndDocumentType(StudyEntity study, DocumentType type);
    List<StudyDocument> findByStudyAndIsCurrent(StudyEntity study, boolean isCurrent);
    List<StudyDocument> findByStudyAndDocumentTypeAndIsCurrent(StudyEntity study, DocumentType type, boolean isCurrent);
}
