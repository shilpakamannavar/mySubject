package com.study.management.infrastructure.web;

import com.study.management.domain.model.DocumentType;
import com.study.management.domain.model.StudyDocument;
import com.study.management.domain.port.in.StudyDocumentUseCase;
import com.study.management.infrastructure.web.dto.CreateDocumentRequest;
import com.study.management.infrastructure.web.dto.DocumentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Study Documents", description = "Study document management")
@RestController
@RequestMapping("/api/v1/studies/{studyId}/documents")
public class StudyDocumentController {

    private final StudyDocumentUseCase documentUseCase;

    public StudyDocumentController(StudyDocumentUseCase documentUseCase) {
        this.documentUseCase = documentUseCase;
    }

    @Operation(summary = "Create a document record for a study")
    @PostMapping
    public ResponseEntity<DocumentResponse> createDocument(@PathVariable UUID studyId,
                                                            @RequestBody CreateDocumentRequest req) {
        StudyDocument doc = new StudyDocument();
        doc.setDocumentType(req.getDocumentType());
        doc.setDocumentName(req.getDocumentName());
        doc.setVersion(req.getVersion());
        doc.setFileUrl(req.getFileUrl());
        doc.setFileSizeBytes(req.getFileSizeBytes());
        doc.setUploadedBy(req.getUploadedBy());
        StudyDocument saved = documentUseCase.createDocument(studyId, doc);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @Operation(summary = "List documents for a study")
    @GetMapping
    public ResponseEntity<List<DocumentResponse>> listDocuments(
            @PathVariable UUID studyId,
            @RequestParam(required = false) DocumentType type,
            @RequestParam(required = false) Boolean current) {
        List<StudyDocument> docs = documentUseCase.listDocuments(studyId, type, current);
        return ResponseEntity.ok(docs.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    private DocumentResponse toResponse(StudyDocument d) {
        DocumentResponse r = new DocumentResponse();
        r.setDocumentId(d.getDocumentId());
        if (d.getStudy() != null) r.setStudyId(d.getStudy().getStudyId());
        r.setDocumentType(d.getDocumentType());
        r.setDocumentName(d.getDocumentName());
        r.setVersion(d.getVersion());
        r.setFileUrl(d.getFileUrl());
        r.setFileSizeBytes(d.getFileSizeBytes());
        r.setUploadedBy(d.getUploadedBy());
        r.setUploadedAt(d.getUploadedAt());
        r.setApprovedAt(d.getApprovedAt());
        r.setIsCurrent(d.getIsCurrent());
        return r;
    }
}
