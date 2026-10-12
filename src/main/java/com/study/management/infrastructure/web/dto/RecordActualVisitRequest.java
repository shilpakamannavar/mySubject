package com.study.management.infrastructure.web.dto;

import java.time.LocalDate;

public class RecordActualVisitRequest {

    private LocalDate actualDate;

    public LocalDate getActualDate()              { return actualDate; }
    public void setActualDate(LocalDate actualDate) { this.actualDate = actualDate; }
}
