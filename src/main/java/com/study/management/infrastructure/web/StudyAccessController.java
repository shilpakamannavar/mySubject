package com.study.management.infrastructure.web;

import com.study.management.domain.model.StudyAccess;
import com.study.management.domain.port.in.StudyAccessUseCase;
import com.study.management.infrastructure.web.dto.AccessResponse;
import com.study.management.infrastructure.web.dto.GrantAccessRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Study Access", description = "Study access control")
@RestController
@RequestMapping("/api/v1/studies/{studyId}/access")
public class StudyAccessController {

    private final StudyAccessUseCase accessUseCase;

    public StudyAccessController(StudyAccessUseCase accessUseCase) {
        this.accessUseCase = accessUseCase;
    }

    @Operation(summary = "Grant access to a study")
    @PostMapping
    public ResponseEntity<AccessResponse> grantAccess(@PathVariable UUID studyId,
                                                       @RequestBody GrantAccessRequest req) {
        StudyAccess access = new StudyAccess();
        access.setUserId(req.getUserId());
        access.setUserRole(req.getUserRole());
        access.setAccessLevel(req.getAccessLevel());
        StudyAccess saved = accessUseCase.grantAccess(studyId, access);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @Operation(summary = "List access for a study")
    @GetMapping
    public ResponseEntity<List<AccessResponse>> listAccess(@PathVariable UUID studyId,
                                                            @RequestParam(required = false) Boolean active) {
        List<StudyAccess> accessList = accessUseCase.listAccess(studyId, active);
        return ResponseEntity.ok(accessList.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @Operation(summary = "Revoke access")
    @DeleteMapping("/{accessId}")
    public ResponseEntity<AccessResponse> revokeAccess(@PathVariable UUID studyId,
                                                        @PathVariable UUID accessId) {
        StudyAccess revoked = accessUseCase.revokeAccess(studyId, accessId);
        return ResponseEntity.ok(toResponse(revoked));
    }

    private AccessResponse toResponse(StudyAccess a) {
        AccessResponse r = new AccessResponse();
        r.setAccessId(a.getAccessId());
        if (a.getStudy() != null) r.setStudyId(a.getStudy().getStudyId());
        r.setUserId(a.getUserId());
        r.setUserRole(a.getUserRole());
        r.setAccessLevel(a.getAccessLevel());
        r.setGrantedAt(a.getGrantedAt());
        r.setRevokedAt(a.getRevokedAt());
        r.setIsActive(a.getIsActive());
        return r;
    }
}
