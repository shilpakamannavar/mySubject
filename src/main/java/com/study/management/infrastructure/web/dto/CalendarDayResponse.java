package com.study.management.infrastructure.web.dto;

import java.time.LocalDate;
import java.util.List;

public class CalendarDayResponse {

    private LocalDate date;
    private String dayOfWeek;
    private int taskCount;
    private List<TaskItem> tasks;

    public LocalDate getDate()              { return date; }
    public void setDate(LocalDate v)        { this.date = v; }

    public String getDayOfWeek()            { return dayOfWeek; }
    public void setDayOfWeek(String v)      { this.dayOfWeek = v; }

    public int getTaskCount()               { return taskCount; }
    public void setTaskCount(int v)         { this.taskCount = v; }

    public List<TaskItem> getTasks()        { return tasks; }
    public void setTasks(List<TaskItem> v)  { this.tasks = v; }
}
