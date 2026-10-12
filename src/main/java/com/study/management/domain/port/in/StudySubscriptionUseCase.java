package com.study.management.domain.port.in;

import com.study.management.domain.model.StudySubscription;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface StudySubscriptionUseCase {

    /** Returns (or previews) current month's subscription for a PI. */
    StudySubscription getPiCurrentMonthSubscription(UUID piId);

    /**
     * PI or SMO (admin) pays the subscription for a PI.
     * paymentDate becomes the subscription start date (defaults to today if null).
     * durationMonths determines coverage period and total charge.
     */
    StudySubscription payPiCurrentMonthSubscription(UUID piId, String paidBy, String paymentReference,
                                                    LocalDate paymentDate, int durationMonths);

    /** Returns all past subscription records for a PI, newest first. */
    List<StudySubscription> getPiSubscriptionHistory(UUID piId);

    /** Used by enrollment: resolves the study's PI then checks if that PI has paid this month. */
    boolean isCurrentMonthPaidForStudy(UUID studyId);
}
