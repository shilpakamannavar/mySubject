package com.study.management.infrastructure.persistence;

import com.study.management.domain.model.PrimaryInvestigator;
import com.study.management.domain.model.StudySubscription;
import com.study.management.domain.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaStudySubscriptionRepository extends JpaRepository<StudySubscription, UUID> {

    Optional<StudySubscription> findByPrimaryInvestigatorAndBillingYearAndBillingMonth(
            PrimaryInvestigator pi, int billingYear, int billingMonth);

    List<StudySubscription> findAllByPrimaryInvestigatorOrderByBillingYearDescBillingMonthDesc(
            PrimaryInvestigator pi);

    // Compare by piId UUID directly to avoid Hibernate proxy entity-comparison issues
    @Query("SELECT s FROM StudySubscription s WHERE s.primaryInvestigator.piId = :piId " +
           "AND s.status = :status " +
           "AND s.subscriptionStartDate <= :date AND s.subscriptionEndDate >= :date")
    Optional<StudySubscription> findFirstByPiAndStatusAndDateCovered(
            @Param("piId") UUID piId,
            @Param("status") SubscriptionStatus status,
            @Param("date") LocalDate date);
}
