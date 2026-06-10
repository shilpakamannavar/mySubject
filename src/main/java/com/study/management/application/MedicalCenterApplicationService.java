package com.study.management.application;

import com.study.management.domain.model.CenterStatus;
import com.study.management.domain.model.MedicalCenter;
import com.study.management.domain.model.Mpa;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.domain.port.in.MedicalCenterUseCase;
import com.study.management.domain.port.out.MedicalCenterPort;
import com.study.management.domain.port.out.MpaPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MedicalCenterApplicationService implements MedicalCenterUseCase {

    private final MedicalCenterPort centerPort;
    private final MpaPort mpaPort;

    public MedicalCenterApplicationService(MedicalCenterPort centerPort, MpaPort mpaPort) {
        this.centerPort = centerPort;
        this.mpaPort = mpaPort;
    }

    @Override
    public MedicalCenter createCenter(UUID mpaId, MedicalCenter center) {
        Mpa mpa = mpaPort.findById(mpaId)
                .orElseThrow(() -> new ResourceNotFoundException("MPA", mpaId));
        center.setMpa(mpa);
        return centerPort.save(center);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalCenter> listCentersForMpa(UUID mpaId, CenterStatus status) {
        Mpa mpa = mpaPort.findById(mpaId)
                .orElseThrow(() -> new ResourceNotFoundException("MPA", mpaId));
        if (status != null) {
            return centerPort.findByMpaAndStatus(mpa, status);
        }
        return centerPort.findByMpa(mpa);
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalCenter getCenterDetail(UUID centerId) {
        return centerPort.findById(centerId)
                .orElseThrow(() -> new ResourceNotFoundException("MedicalCenter", centerId));
    }
}
