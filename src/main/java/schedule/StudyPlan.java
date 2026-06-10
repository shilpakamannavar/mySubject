package schedule;
// StudyPlan.java

import java.util.ArrayList;
import java.util.List;

public class StudyPlan {
    private String name;
    private List<Task> tasks;

    public StudyPlan(String name) {
        this.name = name;
        this.tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public String getName() {
        return name;
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
