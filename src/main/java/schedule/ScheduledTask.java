package schedule;

// ScheduledTask.java

import java.time.LocalDate;

public class ScheduledTask {
    private Task task;
    private LocalDate date;

    public ScheduledTask(Task task, LocalDate date) {
        this.task = task;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Study " + task.getName() + " on " + date;
    }
}
