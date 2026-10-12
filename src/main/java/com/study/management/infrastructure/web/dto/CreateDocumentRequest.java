package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.DocumentType;

import java.util.UUID;

public class CreateDocumentRequest {

    private DocumentType documentType;
    private String documentName;
    private String version;
    private String fileUrl;
    private Long fileSizeBytes;
    private UUID uploadedBy;

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
}
