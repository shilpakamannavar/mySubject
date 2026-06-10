package com.study.management.infrastructure.web.dto;

import java.time.LocalDate;

public class WithdrawRequest {

    private LocalDate withdrawalDate;
    private String withdrawalReason;

    public LocalDate getWithdrawalDate()            { return withdrawalDate; }
    public void setWithdrawalDate(LocalDate v)     { this.withdrawalDate = v; }
    public String getWithdrawalReason()             { return withdrawalReason; }
    public void setWithdrawalReason(String v)      { this.withdrawalReason = v; }
}
