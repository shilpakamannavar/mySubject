package com.study.management.domain.port.in;

import com.study.management.domain.model.DocumentType;
import com.study.management.domain.model.StudyDocument;

import java.util.List;
import java.util.UUID;

public interface StudyDocumentUseCase {
    StudyDocument createDocument(UUID studyId, StudyDocument document);
    List<StudyDocument> listDocuments(UUID studyId, DocumentType type, Boolean current);
}
