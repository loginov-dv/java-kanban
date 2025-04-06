import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    //private final ArrayList<Task> tasks;
    private final HashMap<Integer, Task> basicTasks;
    private final HashMap<Integer, Epic> epicTasks;
    private final HashMap<Integer, Subtask> subtasks;
    //private static int ids = 0;

    TaskManager() {
        //tasks = new ArrayList<>();
        basicTasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
    }

    /*public ArrayList<Task> getAllTasks() {
        return tasks;
    }*/

    public ArrayList<Task> getAllBasicTasks() {
        return new ArrayList<Task>(basicTasks.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<Subtask>(subtasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<Epic>(epicTasks.values());
    }

    public void removeAllBasicTasks() {
        basicTasks.clear();
    }

    public void removeAllSubtasks() {
        // TODO: связи у эпиков
        subtasks.clear();
    }

    public void removeAllEpics() {
        // TODO: связи у сабтасков
        epicTasks.clear();
    }

    public Task getBasicTaskById(int id) {
        return basicTasks.getOrDefault(id, null);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.getOrDefault(id, null);
    }

    public Epic getEpicById(int id) {
        return epicTasks.getOrDefault(id, null);
    }

    public void addBasicTask(Task task) {
        // TODO: проверка на уникальный ключ
        basicTasks.put(task.getId(), task);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    public void addEpicTask(Epic epic) {
        epicTasks.put(epic.getId(), epic);
    }

    public void updateBasicTask(Task updatedTask) {
        /*Task taskToUpdate = basicTasks.getOrDefault(updatedTask.getId(), null);

        if (taskToUpdate == null) {
            return;
        } else {
            basicTasks.replace(updatedTask.getId(), updatedTask);
        }*/
        basicTasks.replace(updatedTask.getId(), updatedTask);
    }

    public void updateSubtask(Subtask updatedTask) {
        /*Subtask taskToUpdate = subtasks.getOrDefault(updatedTask.getId(), null);

        if (taskToUpdate == null) {
            return;
        } else {
            subtasks.replace(updatedTask.getId(), updatedTask);
        }*/
        subtasks.replace(updatedTask.getId(), updatedTask);
    }

    public void updateEpicTask(Epic updatedTask) {
        /*Epic taskToUpdate = epicTasks.getOrDefault(updatedTask.getId(), null);

        if (taskToUpdate == null) {
            return;
        } else {
            epicTasks.replace(updatedTask.getId(), updatedTask);
        }*/
        epicTasks.replace(updatedTask.getId(), updatedTask);
    }

    public void removeBasicTaskById(int id) {
        basicTasks.remove(id);
    }

    public void removeSubtaskById(int id) {
        subtasks.remove(id);
    }

    public void removeEpicTaskById(int id) {
        epicTasks.remove(id);
    }

    // TODO: it was kinda weird, but we're back in the club
    public ArrayList<Subtask> getAllSubtasksOfEpicTask(Epic epic) {
        if (!epicTasks.containsKey(epic.getId())) {
            return null;
        } else {
            return epic.getSubtasks();
        }
    }
}
