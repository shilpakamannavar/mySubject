package com.study.management.application;

import com.study.management.domain.model.DocumentType;
import com.study.management.domain.model.StudyDocument;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.domain.port.in.StudyDocumentUseCase;
import com.study.management.domain.port.out.ClinicalStudyPort;
import com.study.management.domain.port.out.StudyDocumentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class StudyDocumentApplicationService implements StudyDocumentUseCase {

    private final StudyDocumentPort documentPort;
    private final ClinicalStudyPort studyPort;

    public StudyDocumentApplicationService(StudyDocumentPort documentPort, ClinicalStudyPort studyPort) {
        this.documentPort = documentPort;
        this.studyPort = studyPort;
    }

    @Override
    public StudyDocument createDocument(UUID studyId, StudyDocument document) {
        StudyEntity study = studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));
        document.setStudy(study);
        document.setIsCurrent(true);
        return documentPort.save(document);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyDocument> listDocuments(UUID studyId, DocumentType type, Boolean current) {
        StudyEntity study = studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));
        if (type != null && current != null) {
            return documentPort.findByStudyAndTypeAndCurrent(study, type, current);
        } else if (type != null) {
            return documentPort.findByStudyAndType(study, type);
        } else if (current != null) {
            return documentPort.findByStudyAndCurrent(study, current);
        }
        return documentPort.findByStudy(study);
    }
}
