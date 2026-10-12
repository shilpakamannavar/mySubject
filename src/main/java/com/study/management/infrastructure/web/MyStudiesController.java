package com.study.management.infrastructure.web;

import com.study.management.domain.model.ClinicalStudyStatus;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.port.in.ClinicalStudyUseCase;
import com.study.management.infrastructure.web.dto.PagedResponse;
import com.study.management.infrastructure.web.dto.StudyListItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Tag(name = "Investigators", description = "PI study access")
@RestController
public class MyStudiesController {

    private final ClinicalStudyUseCase studyUseCase;

    public MyStudiesController(ClinicalStudyUseCase studyUseCase) {
        this.studyUseCase = studyUseCase;
    }

    @Operation(
        summary = "My studies",
        description = """
            Returns studies visible to the authenticated caller based on their role:

            - **ADMIN** — all studies in the system
            - **PRIMARY_INVESTIGATOR** — studies where they are the assigned PI
            - **CO_INVESTIGATOR** — studies whose study team includes them
            - **CRC** — studies whose study team includes them

            Identity and role are resolved from the JWT — no path or query params needed.
            Use the optional `status` parameter to filter results."""
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR', 'CO_INVESTIGATOR', 'CRC')")
    @GetMapping("/api/v1/studies/my-studies")
    public ResponseEntity<List<StudyListItem>> myStudies(
            @RequestParam(required = false) ClinicalStudyStatus status,
            @AuthenticationPrincipal Jwt jwt) {

        String callerPhone = jwt.getClaimAsString("phone_number");
        String callerRole  = jwt.getClaimAsString("custom:role");

        List<StudyEntity> studies = studyUseCase.getDesignStudies(callerPhone, callerRole);
        if (status != null) {
            studies = studies.stream()
                    .filter(s -> status == s.getStatus())
                    .collect(Collectors.toList());
        }

        List<StudyListItem> items = studies.stream().map(s -> {
            StudyListItem item = new StudyListItem();
            item.setStudyId(s.getStudyId());
            item.setStudyNumber(s.getStudyNumber());
            item.setStudyName(s.getStudyName());
            item.setTitle(s.getTitle());
            item.setPhase(s.getPhase());
            item.setStatus(s.getStatus());
            item.setNoOfPatientsToEnroll(s.getNoOfPatientsToEnroll());
            item.setTotalCost(s.getTotalCost());
            item.setCreatedAt(s.getCreatedAt());
            return item;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(items);
    }

    @Operation(
        summary = "Get studies for a PI",
        description = "Returns studies for the given PI UUID filtered by optional status. " +
                      "ADMIN callers may pass any piId. " +
                      "PRIMARY_INVESTIGATOR callers are always served their own studies — " +
                      "the piId path variable is ignored to prevent cross-PI data access."
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PRIMARY_INVESTIGATOR')")
    @GetMapping("/api/v1/investigators/{piId}/studies")
    public ResponseEntity<PagedResponse<StudyListItem>> getStudiesForPi(
            @PathVariable UUID piId,
            @RequestParam(required = false) ClinicalStudyStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal Jwt jwt) {

        String callerRole  = jwt.getClaimAsString("custom:role");
        String callerPhone = jwt.getClaimAsString("phone_number");

        List<StudyEntity> studies;
        if ("primary-investigator".equals(callerRole)) {
            // Ignore the piId path variable — always use the authenticated caller's identity
            studies = studyUseCase.getStudiesForPiByPhone(callerPhone);
            if (status != null) {
                studies = studies.stream()
                        .filter(s -> status == s.getStatus())
                        .collect(Collectors.toList());
            }
        } else {
            // ADMIN: use the piId from the path to fetch any PI's studies
            studies = studyUseCase.getStudiesForPi(piId, status);
        }

        List<StudyListItem> items = studies.stream().map(s -> {
            StudyListItem item = new StudyListItem();
            item.setStudyId(s.getStudyId());
            item.setStudyNumber(s.getStudyNumber());
            item.setStudyName(s.getStudyName());
            item.setTitle(s.getTitle());
            item.setPhase(s.getPhase());
            item.setStatus(s.getStatus());
            item.setNoOfPatientsToEnroll(s.getNoOfPatientsToEnroll());
            item.setTotalCost(s.getTotalCost());
            item.setCreatedAt(s.getCreatedAt());
            return item;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(PagedResponse.of(items, page, size, items.size()));
    }
}
