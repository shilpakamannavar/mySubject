package com.study.management.application;

import com.study.management.domain.model.ActivationStatus;
import com.study.management.domain.model.Subject;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.domain.port.in.SubjectUseCase;
import com.study.management.domain.port.out.SubjectPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SubjectApplicationService implements SubjectUseCase {

    private final SubjectPort subjectPort;

    public SubjectApplicationService(SubjectPort subjectPort) {
        this.subjectPort = subjectPort;
    }

    @Override
    public Subject create(Subject subject) {
        if (subject.getEmail() != null && subjectPort.existsByEmail(subject.getEmail())) {
            throw new IllegalArgumentException("Subject with email '" + subject.getEmail() + "' already exists.");
        }
        if (subject.getPhone() != null && subjectPort.existsByPhone(subject.getPhone())) {
            throw new IllegalArgumentException("Subject with phone '" + subject.getPhone() + "' already exists.");
        }
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String suffix   = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        subject.setSubjectNumber("SUB-" + datePart + "-" + suffix);
        return subjectPort.save(subject);
    }

    @Override
    @Transactional(readOnly = true)
    public Subject getById(UUID id) {
        return subjectPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subject> listAll() {
        return subjectPort.findAll();
    }

    @Override
    public Subject generateQrToken(String phone) {
        Subject subject = subjectPort.findByPhone(phone)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No subject found for this phone number."));
        String securityToken = UUID.randomUUID().toString();
        // QR payload includes subjectId so scanners can display identity alongside the token
        String qrPayload = "{\"subjectId\":\"" + subject.getSubjectId()
                + "\",\"token\":\"" + securityToken + "\"}";
        subject.setQrToken(qrPayload);
        subject.setQrTokenExpiresAt(OffsetDateTime.now().plusHours(24));
        return subjectPort.save(subject);
    }

    @Override
    public Subject activateByQrToken(String qrToken, String activatedByPhone) {
        Subject subject = subjectPort.findByQrToken(qrToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "QR code not found or already used."));
        if (subject.getQrTokenExpiresAt() == null || OffsetDateTime.now().isAfter(subject.getQrTokenExpiresAt())) {
            throw new ResponseStatusException(HttpStatus.GONE,
                    "QR code has expired. The subject must generate a new one.");
        }
        subject.setActivationStatus(ActivationStatus.ACTIVE);
        subject.setActivatedAt(OffsetDateTime.now());
        subject.setActivatedBy(activatedByPhone);
        // One-time use: clear the token after activation
        subject.setQrToken(null);
        subject.setQrTokenExpiresAt(null);
        return subjectPort.save(subject);
    }

    @Override
    public Subject linkToPatient(UUID subjectId, UUID patientId) {
        Subject subject = subjectPort.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", subjectId));
        subject.setPatientId(patientId);
        return subjectPort.save(subject);
    }

    public Subject deactivate(UUID subjectId, String deactivatedByPhone) {
        Subject subject = subjectPort.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", subjectId));
        subject.setActivationStatus(ActivationStatus.INACTIVE);
        subject.setDeactivatedAt(OffsetDateTime.now());
        subject.setDeactivatedBy(deactivatedByPhone);
        return subjectPort.save(subject);
    }
}