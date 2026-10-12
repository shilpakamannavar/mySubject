package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.StudySubscription;
import com.study.management.domain.model.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class StudySubscriptionResponse {

    private UUID subscriptionId;
    private UUID piId;
    private String piNumber;
    private String piName;
    private int billingYear;
    private int billingMonth;
    private int durationMonths;
    private LocalDate subscriptionStartDate;
    private LocalDate subscriptionEndDate;
    private BigDecimal amount;
    private SubscriptionStatus status;
    private OffsetDateTime paidAt;
    private String paidBy;
    private String paymentReference;
    private OffsetDateTime createdAt;

    public static StudySubscriptionResponse from(StudySubscription s) {
        StudySubscriptionResponse r = new StudySubscriptionResponse();
        r.subscriptionId        = s.getSubscriptionId();
        r.billingYear           = s.getBillingYear();
        r.billingMonth          = s.getBillingMonth();
        r.durationMonths        = s.getDurationMonths() != null ? s.getDurationMonths() : 1;
        r.subscriptionStartDate = s.getSubscriptionStartDate();
        r.subscriptionEndDate   = s.getSubscriptionEndDate();
        r.amount                = s.getAmount();
        r.status                = s.getStatus();
        r.paidAt           = s.getPaidAt();
        r.paidBy           = s.getPaidBy();
        r.paymentReference = s.getPaymentReference();
        r.createdAt        = s.getCreatedAt();
        if (s.getPrimaryInvestigator() != null) {
            r.piId     = s.getPrimaryInvestigator().getPiId();
            r.piNumber = s.getPrimaryInvestigator().getPiNumber();
            r.piName   = s.getPrimaryInvestigator().getFirstName()
                         + " " + s.getPrimaryInvestigator().getLastName();
        }
        return r;
    }

    public UUID getSubscriptionId()              { return subscriptionId; }
    public UUID getPiId()                        { return piId; }
    public String getPiNumber()                  { return piNumber; }
    public String getPiName()                    { return piName; }
    public int getBillingYear()                  { return billingYear; }
    public int getBillingMonth()                 { return billingMonth; }
    public int getDurationMonths()               { return durationMonths; }
    public LocalDate getSubscriptionStartDate()  { return subscriptionStartDate; }
    public LocalDate getSubscriptionEndDate()    { return subscriptionEndDate; }
    public BigDecimal getAmount()                { return amount; }
    public SubscriptionStatus getStatus()        { return status; }
    public OffsetDateTime getPaidAt()            { return paidAt; }
    public String getPaidBy()                    { return paidBy; }
    public String getPaymentReference()          { return paymentReference; }
    public OffsetDateTime getCreatedAt()         { return createdAt; }
}
