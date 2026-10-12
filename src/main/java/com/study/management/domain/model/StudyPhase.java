package com.study.management.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Map;

public enum StudyPhase {
    PHASE_I, PHASE_II, PHASE_III, PHASE_IV, OBSERVATIONAL;

    private static final Map<String, StudyPhase> ALIASES = Map.of(
            "PHASE_1", PHASE_I,
            "PHASE_2", PHASE_II,
            "PHASE_3", PHASE_III,
            "PHASE_4", PHASE_IV
    );

    @JsonCreator
    public static StudyPhase fromValue(String value) {
        if (value == null) return null;
        String normalized = value.trim().toUpperCase();
        StudyPhase alias = ALIASES.get(normalized);
        if (alias != null) return alias;
        return StudyPhase.valueOf(normalized);
    }
}
