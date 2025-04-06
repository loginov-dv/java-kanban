import java.util.ArrayList;

public class TaskManager {
    private final ArrayList<Task> tasks;

    TaskManager() {
        tasks = new ArrayList<>();
    }

    public ArrayList<Task> getAllTasks() {
        return tasks;
    }

    public ArrayList<Task> getAllBasicTasks() {
        ArrayList<Task> basicTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.getClass() == Task.class) {
                basicTasks.add(task);
            }
        }

        return basicTasks;
    }

    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> subtasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.getClass() == Subtask.class) {
                subtasks.add((Subtask)task);
            }
        }

        return subtasks;
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> epics = new ArrayList<>();

        for (Task task : tasks) {
            if (task.getClass() == Epic.class) {
                epics.add((Epic)task);
            }
        }

        return epics;
    }

    public void removeAllBasicTasks() {
        /*for (Task task : tasks) {
            if (task.getClass() != Epic.class && task.getClass() != Subtask.class) {
                basicTasks.add(task);
            }
        }*/
    }

    public void removeAllSubtasks() {
        // TODO: связи у эпиков
    }

    public void removeAllEpics() {
        // TODO: связи у сабтасков
    }

    public Task getTaskById(int id) {
        // TODO: методы для других типов?

        for (Task task : tasks) {
            if (task.getId() == id) {
                return task;
            }
        }

        return null;
    }

    public void addTask(Task task) {
        // TODO: методы для других типов
        tasks.add(task);
    }

    public void updateTask(Task updatedTask) {
        // TODO: методы для других типов
        Task taskToUpdate = null;

        for (Task task : tasks) {
            if (task.getId() == updatedTask.getId()) {
                taskToUpdate = task;
            }
        }

        if (taskToUpdate == null) {
            return;
        } else {
            tasks.remove(taskToUpdate);
            tasks.add(updatedTask);
        }
    }

    public void removeTaskById(int id) {
        // TODO: методы для других типов
        Task taskToRemove = null;

        for (Task task : tasks) {
            if (task.getId() == id) {
                taskToRemove = task;
            }
        }

        if (taskToRemove == null) {
            return;
        } else {
            tasks.remove(taskToRemove);
        }
    }

    public ArrayList<Subtask> getAllSubtasksOfEpic(Epic epic) {
        if (!tasks.contains(epic)) {
            return null;
        } else {
            return epic.getSubtasks();
        }
    }
}
