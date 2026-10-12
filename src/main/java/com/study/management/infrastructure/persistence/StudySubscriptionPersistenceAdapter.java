package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.PrimaryInvestigator;
import com.study.management.domain.model.StudySubscription;
import com.study.management.domain.model.SubscriptionStatus;
import com.study.management.domain.port.out.StudySubscriptionPort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class StudySubscriptionPersistenceAdapter implements StudySubscriptionPort {

    private final JpaStudySubscriptionRepository repository;

    public StudySubscriptionPersistenceAdapter(JpaStudySubscriptionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<StudySubscription> findByPiAndMonth(PrimaryInvestigator pi, int year, int month) {
        return repository.findByPrimaryInvestigatorAndBillingYearAndBillingMonth(pi, year, month);
    }

    @Override
    public StudySubscription save(StudySubscription subscription) {
        return repository.save(subscription);
    }

    @Override
    public Optional<StudySubscription> findPaidCoveringDate(PrimaryInvestigator pi, LocalDate date) {
        return repository.findFirstByPiAndStatusAndDateCovered(pi.getPiId(), SubscriptionStatus.PAID, date);
    }

    @Override
    public List<StudySubscription> findAllByPi(PrimaryInvestigator pi) {
        return repository.findAllByPrimaryInvestigatorOrderByBillingYearDescBillingMonthDesc(pi);
    }
}
