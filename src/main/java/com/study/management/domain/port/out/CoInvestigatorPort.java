package com.study.management.domain.port.out;

import com.study.management.domain.model.CoInvestigator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CoInvestigatorPort {
    CoInvestigator save(CoInvestigator ci);
    Optional<CoInvestigator> findById(UUID id);
    List<CoInvestigator> findAll();
    Optional<CoInvestigator> findByPhone(String phone);
    Optional<CoInvestigator> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
