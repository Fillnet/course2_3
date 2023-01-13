package Notebook;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class Schedule {
    private final Map<Integer, Task> tasks = new HashMap<>();

    public void addTask(Task task) {
        this.tasks.put(task.getId(), task);
    }

    public void removeTask(int id) throws TaskNotFoundException {
        if (this.tasks.containsKey(id)) {
            this.tasks.remove(id);
        } else {
            throw new TaskNotFoundException();
        }
    }

    public Collection<Task> getAllTask() {
        return this.tasks.values();
    }

    public Collection<Task> getTaskForDate(LocalDate date) {
        TreeSet<Task> taskForDate = new TreeSet<>();
        for (Task task : tasks.values()) {
            if (task.appearsIn(date)) {
                taskForDate.add(task);
            }
        }
        return taskForDate;
    }
}

