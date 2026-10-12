package com.study.management.domain.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "study_documents")
public class StudyDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "document_id")
    private UUID documentId;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private StudyEntity study;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type")
    private DocumentType documentType;

    @Column(name = "document_name")
    private String documentName;

    @Column(name = "version")
    private String version;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "uploaded_by")
    private UUID uploadedBy;

    @Column(name = "uploaded_at")
    private OffsetDateTime uploadedAt;

    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;

    @Column(name = "is_current")
    private Boolean isCurrent;

    @PrePersist
    private void onCreate() {
        if (uploadedAt == null) uploadedAt = OffsetDateTime.now();
        if (isCurrent == null) isCurrent = true;
    }

    // ── Getters & Setters ──────────────────────────────────────────────

    public UUID getDocumentId()                         { return documentId; }
    public void setDocumentId(UUID v)                  { this.documentId = v; }

    public StudyEntity getStudy()                       { return study; }
    public void setStudy(StudyEntity study)            { this.study = study; }

    public DocumentType getDocumentType()               { return documentType; }
    public void setDocumentType(DocumentType v)        { this.documentType = v; }

    public String getDocumentName()                     { return documentName; }
    public void setDocumentName(String v)              { this.documentName = v; }

    public String getVersion()                          { return version; }
    public void setVersion(String version)             { this.version = version; }

    public String getFileUrl()                          { return fileUrl; }
    public void setFileUrl(String v)                   { this.fileUrl = v; }

    public Long getFileSizeBytes()                      { return fileSizeBytes; }
    public void setFileSizeBytes(Long v)               { this.fileSizeBytes = v; }

    public UUID getUploadedBy()                         { return uploadedBy; }
    public void setUploadedBy(UUID v)                  { this.uploadedBy = v; }

    public OffsetDateTime getUploadedAt()               { return uploadedAt; }
    public void setUploadedAt(OffsetDateTime v)        { this.uploadedAt = v; }

    public OffsetDateTime getApprovedAt()               { return approvedAt; }
    public void setApprovedAt(OffsetDateTime v)        { this.approvedAt = v; }

    public Boolean getIsCurrent()                       { return isCurrent; }
    public void setIsCurrent(Boolean v)                { this.isCurrent = v; }
}
