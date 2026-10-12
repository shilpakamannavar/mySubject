package com.study.management.application;

import com.study.management.domain.model.Mpa;
import com.study.management.domain.model.MpaStatus;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.domain.port.in.MpaUseCase;
import com.study.management.domain.port.out.MpaPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MpaApplicationService implements MpaUseCase {

    private final MpaPort mpaPort;

    public MpaApplicationService(MpaPort mpaPort) {
        this.mpaPort = mpaPort;
    }

    @Override
    public Mpa createMpa(Mpa mpa) {
        if (mpa.getMpaNumber() != null && mpaPort.existsByMpaNumber(mpa.getMpaNumber())) {
            throw new IllegalArgumentException("MPA with number '" + mpa.getMpaNumber() + "' already exists.");
        }
        return mpaPort.save(mpa);
    }

    @Override
    @Transactional(readOnly = true)
    public Mpa getById(UUID mpaId) {
        return mpaPort.findById(mpaId)
                .orElseThrow(() -> new ResourceNotFoundException("MPA", mpaId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Mpa> listAll(MpaStatus status) {
        if (status != null) {
            return mpaPort.findByStatus(status);
        }
        return mpaPort.findAll();
    }

    @Override
    public Mpa update(UUID mpaId, Mpa updates) {
        Mpa mpa = getById(mpaId);
        if (updates.getMpaName() != null)        mpa.setMpaName(updates.getMpaName());
        if (updates.getExpirationDate() != null) mpa.setExpirationDate(updates.getExpirationDate());
        if (updates.getContactName() != null)    mpa.setContactName(updates.getContactName());
        if (updates.getContactEmail() != null)   mpa.setContactEmail(updates.getContactEmail());
        if (updates.getNotes() != null)          mpa.setNotes(updates.getNotes());
        return mpaPort.save(mpa);
    }
}
