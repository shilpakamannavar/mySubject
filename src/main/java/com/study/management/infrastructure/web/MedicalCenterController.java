package com.study.management.infrastructure.web;

import com.study.management.domain.model.CenterStatus;
import com.study.management.domain.model.MedicalCenter;
import com.study.management.domain.model.PiCenterAssignment;
import com.study.management.domain.port.in.MedicalCenterUseCase;
import com.study.management.domain.port.out.InvestigatorPort;
import com.study.management.infrastructure.web.dto.CenterDetailResponse;
import com.study.management.infrastructure.web.dto.CreateMedicalCenterRequest;
import com.study.management.infrastructure.web.dto.MedicalCenterResponse;
import com.study.management.infrastructure.web.dto.PagedResponse;
import com.study.management.infrastructure.web.dto.PiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Tag(name = "Medical Centers", description = "Medical center management")
@RestController
public class MedicalCenterController {

    private final MedicalCenterUseCase centerUseCase;
    private final InvestigatorPort investigatorPort;

    public MedicalCenterController(MedicalCenterUseCase centerUseCase, InvestigatorPort investigatorPort) {
        this.centerUseCase = centerUseCase;
        this.investigatorPort = investigatorPort;
    }

    @Operation(summary = "Create a medical center under an MPA")
    @PostMapping("/api/v1/mpa/{mpaId}/centers")
    public ResponseEntity<MedicalCenterResponse> createCenter(@PathVariable UUID mpaId,
                                                               @RequestBody CreateMedicalCenterRequest req) {
        MedicalCenter center = fromRequest(req);
        MedicalCenter saved = centerUseCase.createCenter(mpaId, center);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @Operation(summary = "List centers for MPA")
    @GetMapping("/api/v1/mpa/{mpaId}/centers")
    public ResponseEntity<PagedResponse<MedicalCenterResponse>> listCenters(
            @PathVariable UUID mpaId,
            @RequestParam(required = false) CenterStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<MedicalCenter> centers = centerUseCase.listCentersForMpa(mpaId, status);
        List<MedicalCenterResponse> items = centers.stream().map(c -> {
            MedicalCenterResponse r = toResponse(c);
            List<PiCenterAssignment> assignments = investigatorPort.findAssignmentsByCenter(c);
            r.setPiCount(assignments.size());
            return r;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(PagedResponse.of(items, page, size, items.size()));
    }

    @Operation(summary = "Get center detail with investigators")
    @GetMapping("/api/v1/centers/{centerId}")
    public ResponseEntity<CenterDetailResponse> getCenterDetail(@PathVariable UUID centerId) {
        MedicalCenter center = centerUseCase.getCenterDetail(centerId);
        CenterDetailResponse resp = new CenterDetailResponse();
        copyToResponse(center, resp);

        List<PiCenterAssignment> assignments = investigatorPort.findAssignmentsByCenter(center);
        List<PiResponse> investigators = assignments.stream().map(a -> {
            PiResponse pr = new PiResponse();
            var pi = a.getPrimaryInvestigator();
            pr.setPiId(pi.getPiId());
            pr.setPiNumber(pi.getPiNumber());
            pr.setFirstName(pi.getFirstName());
            pr.setLastName(pi.getLastName());
            pr.setTitle(pi.getTitle());
            pr.setCredentials(pi.getCredentials());
            pr.setSpecialty(pi.getSpecialty());
            pr.setEmail(pi.getEmail());
            pr.setPhone(pi.getPhone());
            pr.setStatus(pi.getStatus());
            return pr;
        }).collect(Collectors.toList());
        resp.setInvestigators(investigators);
        resp.setPiCount(investigators.size());
        return ResponseEntity.ok(resp);
    }

    private MedicalCenter fromRequest(CreateMedicalCenterRequest req) {
        MedicalCenter c = new MedicalCenter();
        c.setCenterCode(req.getCenterCode());
        c.setCenterName(req.getCenterName());
        c.setCenterType(req.getCenterType());
        c.setAddressLine1(req.getAddressLine1());
        c.setAddressLine2(req.getAddressLine2());
        c.setCity(req.getCity());
        c.setState(req.getState());
        c.setZipCode(req.getZipCode());
        c.setCountry(req.getCountry());
        c.setPhone(req.getPhone());
        c.setFax(req.getFax());
        c.setEmail(req.getEmail());
        c.setIrbName(req.getIrbName());
        c.setIrbApprovalNumber(req.getIrbApprovalNumber());
        c.setIrbApprovalDate(req.getIrbApprovalDate());
        c.setIrbExpiryDate(req.getIrbExpiryDate());
        return c;
    }

    private MedicalCenterResponse toResponse(MedicalCenter c) {
        MedicalCenterResponse r = new MedicalCenterResponse();
        copyToResponse(c, r);
        return r;
    }

    private void copyToResponse(MedicalCenter c, MedicalCenterResponse r) {
        r.setCenterId(c.getCenterId());
        if (c.getMpa() != null) {
            r.setMpaId(c.getMpa().getMpaId());
            r.setMpaName(c.getMpa().getMpaName());
        }
        r.setCenterCode(c.getCenterCode());
        r.setCenterName(c.getCenterName());
        r.setCenterType(c.getCenterType());
        r.setAddressLine1(c.getAddressLine1());
        r.setAddressLine2(c.getAddressLine2());
        r.setCity(c.getCity());
        r.setState(c.getState());
        r.setZipCode(c.getZipCode());
        r.setCountry(c.getCountry());
        r.setPhone(c.getPhone());
        r.setFax(c.getFax());
        r.setEmail(c.getEmail());
        r.setIrbName(c.getIrbName());
        r.setIrbApprovalNumber(c.getIrbApprovalNumber());
        r.setIrbApprovalDate(c.getIrbApprovalDate());
        r.setIrbExpiryDate(c.getIrbExpiryDate());
        r.setStatus(c.getStatus());
        r.setCreatedAt(c.getCreatedAt());
        r.setUpdatedAt(c.getUpdatedAt());
    }
}
