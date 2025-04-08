package ru.yandex.practicum.TaskManager;

import ru.yandex.practicum.Tasks.*;

import java.util.ArrayList;
import java.util.HashMap;

// Класс для описания трекера задач
public class TaskManager {
    private final HashMap<Integer, Task> basicTasks;
    private final HashMap<Integer, Epic> epicTasks;
    private final HashMap<Integer, Subtask> subtasks;
    private static int globalId;

    public TaskManager() {
        basicTasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subtasks = new HashMap<>();
        globalId = 0;
    }

    public int nextId() {
        return ++globalId;
    }

    // Получение списка всех задач (обычных)
    public ArrayList<Task> getAllBasicTasks() {
        return new ArrayList<Task>(basicTasks.values());
    }

    // Получение списка всех подзадач
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<Subtask>(subtasks.values());
    }

    // Получение списка всех эпиков
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<Epic>(epicTasks.values());
    }

    // Удаление всех задач (обычных)
    public void removeAllBasicTasks() {
        basicTasks.clear();
    }

    // Удаление всех подзадач
    public void removeAllSubtasks() {
        // Удаляем у эпиков ссылки на подзадачи
        for (Subtask subtask : subtasks.values()) {
            subtask.removeEpic();
        }
        subtasks.clear();
    }

    // Удаление всех эпиков
    public void removeAllEpics() {
        // Полагаем, что подзадачи не имеют смысла без эпика
        subtasks.clear();
        epicTasks.clear();
    }

    // Получение задачи (обычной) по идентификатору
    public Task getBasicTaskById(int id) {
        return basicTasks.getOrDefault(id, null);
    }

    // Получение подзадачи по идентификатору
    public Subtask getSubtaskById(int id) {
        return subtasks.getOrDefault(id, null);
    }

    // Получение эпика по идентификатору
    public Epic getEpicById(int id) {
        return epicTasks.getOrDefault(id, null);
    }

    // Добавление задачи (обычной)
    public void addBasicTask(Task task) {
        basicTasks.put(task.getId(), task);
    }

    // Добавление подзадачи
    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    // Добавление эпика
    public void addEpicTask(Epic epic) {
        epicTasks.put(epic.getId(), epic);
    }

    // Обновление задачи (обычной)
    public void updateBasicTask(Task updatedTask) {
        basicTasks.replace(updatedTask.getId(), updatedTask);
    }

    // Обновление подзадачи
    public void updateSubtask(Subtask updatedTask) {
        subtasks.replace(updatedTask.getId(), updatedTask);
    }

    // Обновление эпика
    public void updateEpicTask(Epic updatedTask) {
        epicTasks.replace(updatedTask.getId(), updatedTask);
    }

    // Удаление задачи (обычной) по идентификатору
    public void removeBasicTaskById(int id) {
        basicTasks.remove(id);
    }

    // Удаление подзадачи по идентификатору
    public void removeSubtaskById(int id) {
        subtasks.remove(id);
    }

    // Удаление эпика по идентификатору
    public void removeEpicTaskById(int id) {
        Epic epic = epicTasks.get(id);
        if (epic == null) {
            return;
        }
        for (Subtask subtask : epic.getAllSubtasks()) {
            removeSubtaskById(subtask.getId());
        }
        epicTasks.remove(id);
    }

    // Получение всех подзадач для указанного эпика
    public ArrayList<Subtask> getAllSubtasksOfEpicTask(Epic epic) {
        if (!epicTasks.containsKey(epic.getId())) {
            return null;
        } else {
            return epic.getAllSubtasks();
        }
    }
}
