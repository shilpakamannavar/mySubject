package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.DocumentType;

import java.time.OffsetDateTime;
import java.util.UUID;

public class DocumentResponse {

    private UUID documentId;
    private UUID studyId;
    private DocumentType documentType;
    private String documentName;
    private String version;
    private String fileUrl;
    private Long fileSizeBytes;
    private UUID uploadedBy;
    private OffsetDateTime uploadedAt;
    private OffsetDateTime approvedAt;
    private Boolean isCurrent;

    public UUID getDocumentId()                     { return documentId; }
    public void setDocumentId(UUID v)              { this.documentId = v; }
    public UUID getStudyId()                        { return studyId; }
    public void setStudyId(UUID v)                 { this.studyId = v; }
    public DocumentType getDocumentType()           { return documentType; }
    public void setDocumentType(DocumentType v)    { this.documentType = v; }
    public String getDocumentName()                 { return documentName; }
    public void setDocumentName(String v)          { this.documentName = v; }
    public String getVersion()                      { return version; }
    public void setVersion(String v)               { this.version = v; }
    public String getFileUrl()                      { return fileUrl; }
    public void setFileUrl(String v)               { this.fileUrl = v; }
    public Long getFileSizeBytes()                  { return fileSizeBytes; }
    public void setFileSizeBytes(Long v)           { this.fileSizeBytes = v; }
    public UUID getUploadedBy()                     { return uploadedBy; }
    public void setUploadedBy(UUID v)              { this.uploadedBy = v; }
    public OffsetDateTime getUploadedAt()           { return uploadedAt; }
    public void setUploadedAt(OffsetDateTime v)    { this.uploadedAt = v; }
    public OffsetDateTime getApprovedAt()           { return approvedAt; }
    public void setApprovedAt(OffsetDateTime v)    { this.approvedAt = v; }
    public Boolean getIsCurrent()                   { return isCurrent; }
    public void setIsCurrent(Boolean v)            { this.isCurrent = v; }
}
