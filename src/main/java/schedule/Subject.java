package schedule;

// Student.java

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Subject {
    private String name;
    private LocalDate startDate;
    private List<ScheduledTask> scheduledTasks;

    public Subject(String name, LocalDate startDate) {
        this.name = name;
        this.startDate = startDate;
        this.scheduledTasks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void addScheduledTask(ScheduledTask task) {
        this.scheduledTasks.add(task);
    }

    public List<ScheduledTask> getScheduledTasks() {
        return scheduledTasks;
    }
}
