package schedule;

// Task.java
public class Task {
    private String name;
    private int durationDays;

    public Task(String name, int durationDays) {
        this.name = name;
        this.durationDays = durationDays;
    }

    public String getName() {
        return name;
    }

    public int getDurationDays() {
        return durationDays;
    }

    @Override
    public String toString() {
        return name + " (" + durationDays + " days)";
    }
}
