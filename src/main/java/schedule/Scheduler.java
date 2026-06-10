package schedule;

// Scheduler.java

import java.time.LocalDate;

public class Scheduler {
    public void applyStudyPlan(Subject subject, StudyPlan plan) {
        LocalDate currentDate = subject.getStartDate();

        System.out.println("--- Scheduling plan for " + subject.getName() + " ---");

        for (Task task : plan.getTasks()) {
            // Schedule the task for the appropriate duration
            for (int i = 0; i < task.getDurationDays(); i++) {
                subject.addScheduledTask(new ScheduledTask(task, currentDate));
                currentDate = currentDate.plusDays(1);
            }
        }
    }

    public void printStudentSchedule(Subject student) {
        System.out.println("\nCalendar for " + student.getName() + " (starting " + student.getStartDate() + "):");
        for (ScheduledTask scheduledTask : student.getScheduledTasks()) {
            System.out.println(scheduledTask);
        }
    }
}
