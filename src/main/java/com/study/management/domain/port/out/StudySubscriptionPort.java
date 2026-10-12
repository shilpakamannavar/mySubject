package com.study.management.domain.port.out;

import com.study.management.domain.model.PrimaryInvestigator;
import com.study.management.domain.model.StudySubscription;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudySubscriptionPort {
    Optional<StudySubscription> findByPiAndMonth(PrimaryInvestigator pi, int year, int month);
    /** Returns a PAID subscription whose coverage window contains the given date, if any. */
    Optional<StudySubscription> findPaidCoveringDate(PrimaryInvestigator pi, LocalDate date);
    StudySubscription save(StudySubscription subscription);
    List<StudySubscription> findAllByPi(PrimaryInvestigator pi);
}
