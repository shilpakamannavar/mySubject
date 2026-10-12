package com.study.management.application;

import com.study.management.domain.model.PrimaryInvestigator;
import com.study.management.domain.model.StudyEntity;
import com.study.management.domain.model.StudySubscription;
import com.study.management.domain.model.SubscriptionConfig;
import com.study.management.domain.model.SubscriptionStatus;
import com.study.management.domain.model.exception.ResourceNotFoundException;
import com.study.management.domain.port.in.StudySubscriptionUseCase;
import com.study.management.domain.port.out.ClinicalStudyPort;
import com.study.management.domain.port.out.InvestigatorPort;
import com.study.management.domain.port.out.StudySubscriptionPort;
import com.study.management.domain.port.out.SubscriptionConfigPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class StudySubscriptionApplicationService implements StudySubscriptionUseCase {

    private final StudySubscriptionPort subscriptionPort;
    private final InvestigatorPort investigatorPort;
    private final ClinicalStudyPort studyPort;
    private final SubscriptionConfigPort configPort;

    public StudySubscriptionApplicationService(StudySubscriptionPort subscriptionPort,
                                               InvestigatorPort investigatorPort,
                                               ClinicalStudyPort studyPort,
                                               SubscriptionConfigPort configPort) {
        this.subscriptionPort  = subscriptionPort;
        this.investigatorPort  = investigatorPort;
        this.studyPort         = studyPort;
        this.configPort        = configPort;
    }

    @Override
    @Transactional(readOnly = true)
    public StudySubscription getPiCurrentMonthSubscription(UUID piId) {
        PrimaryInvestigator pi = resolvePi(piId);
        LocalDate today = LocalDate.now();
        // Return active paid subscription covering today, else the pending start-of-month record, else a preview
        return subscriptionPort.findPaidCoveringDate(pi, today)
                .or(() -> subscriptionPort.findByPiAndMonth(pi, today.getYear(), today.getMonthValue()))
                .orElseGet(() -> buildPending(pi, today, 1));
    }

    @Override
    public StudySubscription payPiCurrentMonthSubscription(UUID piId, String paidBy, String paymentReference,
                                                           LocalDate paymentDate, int durationMonths) {
        PrimaryInvestigator pi = resolvePi(piId);
        LocalDate startDate = (paymentDate != null) ? paymentDate : LocalDate.now();
        int months = (durationMonths < 1) ? 1 : durationMonths;

        StudySubscription sub = subscriptionPort.findByPiAndMonth(pi, startDate.getYear(), startDate.getMonthValue())
                .orElseGet(() -> buildPending(pi, startDate, months));

        if (sub.getStatus() == SubscriptionStatus.PAID) {
            throw new IllegalStateException("A subscription starting in "
                    + startDate.getYear() + "-" + startDate.getMonthValue()
                    + " is already paid for PI " + pi.getPiNumber() + ".");
        }

        SubscriptionConfig config = configPort.find();
        BigDecimal rate = (config != null) ? config.getMonthlyRate() : new BigDecimal("299.00");

        sub.setDurationMonths(months);
        sub.setSubscriptionStartDate(startDate);
        sub.setSubscriptionEndDate(startDate.plusMonths(months).minusDays(1));
        sub.setAmount(rate.multiply(BigDecimal.valueOf(months)));
        sub.setStatus(SubscriptionStatus.PAID);
        sub.setPaidAt(OffsetDateTime.now());
        sub.setPaidBy(paidBy);
        sub.setPaymentReference(paymentReference != null ? paymentReference
                : "SUB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        return subscriptionPort.save(sub);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudySubscription> getPiSubscriptionHistory(UUID piId) {
        PrimaryInvestigator pi = resolvePi(piId);
        return subscriptionPort.findAllByPi(pi);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCurrentMonthPaidForStudy(UUID studyId) {
        StudyEntity study = studyPort.findById(studyId)
                .orElseThrow(() -> new ResourceNotFoundException("Study", studyId));

        PrimaryInvestigator pi = study.getCreatedByPi();
        if (pi == null) {
            return false;
        }

        return subscriptionPort.findPaidCoveringDate(pi, LocalDate.now()).isPresent();
    }

    private PrimaryInvestigator resolvePi(UUID piId) {
        return investigatorPort.findById(piId)
                .orElseThrow(() -> new ResourceNotFoundException("PrimaryInvestigator", piId));
    }

    private StudySubscription buildPending(PrimaryInvestigator pi, LocalDate startDate, int durationMonths) {
        SubscriptionConfig config = configPort.find();
        BigDecimal rate = (config != null) ? config.getMonthlyRate() : new BigDecimal("299.00");
        StudySubscription sub = new StudySubscription();
        sub.setPrimaryInvestigator(pi);
        sub.setBillingYear(startDate.getYear());
        sub.setBillingMonth(startDate.getMonthValue());
        sub.setDurationMonths(durationMonths);
        sub.setSubscriptionStartDate(startDate);
        sub.setSubscriptionEndDate(startDate.plusMonths(durationMonths).minusDays(1));
        sub.setAmount(rate.multiply(BigDecimal.valueOf(durationMonths)));
        sub.setStatus(SubscriptionStatus.PENDING);
        return sub;
    }
}
