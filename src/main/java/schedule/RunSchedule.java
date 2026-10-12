package schedule;

// Main.java

import java.time.LocalDate;

public class RunSchedule {
    public static void main(String[] args) {
        // 1. Compose the study plan
        StudyPlan javaStudyPlan = new StudyPlan("Java Basics");
        javaStudyPlan.addTask(new Task("Prelim", 2));
        javaStudyPlan.addTask(new Task("Gap", 3));
        javaStudyPlan.addTask(new Task("Visit 1", 2));
        javaStudyPlan.addTask(new Task("Gap 1", 3));
        javaStudyPlan.addTask(new Task("Visit 2", 5));




        javaStudyPlan.addTask(new Task("Gap 2", 3));
        javaStudyPlan.addTask(new Task("Visit 3", 2));
        javaStudyPlan.addTask(new Task("Gap 3", 3));
        javaStudyPlan.addTask(new Task("Visit 4", 5));
        javaStudyPlan.addTask(new Task("Gap 4", 3));

        // 2. Create the students with different start dates
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusWeeks(1);
        LocalDate nextMonth = today.plusMonths(1);

        Subject alice = new Subject("Ram", today);
        Subject bob = new Subject("Ramesh", nextWeek);
        Subject charlie = new Subject("Vidya", nextMonth);

        // 3. Apply the study plan to each student's calendar
        Scheduler scheduler = new Scheduler();

        scheduler.applyStudyPlan(alice, javaStudyPlan);
        scheduler.applyStudyPlan(bob, javaStudyPlan);
        scheduler.applyStudyPlan(charlie, javaStudyPlan);

        // 4. Print the calendars for each student
        scheduler.printStudentSchedule(alice);
        scheduler.printStudentSchedule(bob);
        scheduler.printStudentSchedule(charlie);
    }
}
