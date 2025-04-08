package ru.yandex.practicum.TaskManager;

import ru.yandex.practicum.Tasks.*;

import java.util.ArrayList;
import java.util.HashMap;

// Класс для описания трекера задач
public class TaskManager {
    // Обычные задачи
    private final HashMap<Integer, Task> basicTasks;
    // Эпики
    private final HashMap<Integer, Epic> epics;
    // Подзадачи
    private final HashMap<Integer, Subtask> subtasks;
    // Последний присвоенный id
    private static int globalId;

    // Конструктор класса TaskManager
    public TaskManager() {
        basicTasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        globalId = 0;
    }

    // Получение нового идентификатора
    public int nextId() {
        return ++globalId;
    }

    // Получение списка всех задач (обычных)
    public ArrayList<Task> getAllBasicTasks() {
        return new ArrayList<>(basicTasks.values());
    }
    // Получение списка всех подзадач
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }
    // Получение списка всех эпиков
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    // Удаление всех задач (обычных)
    public void removeAllBasicTasks() {
        basicTasks.clear();
    }
    // Удаление всех подзадач
    public void removeAllSubtasks() {

        // TODO:
        // Удаляем у эпиков ссылки на подзадачи
        /*for (Subtask subtask : subtasks.values()) {
            subtask.removeEpic();
        }*/
        subtasks.clear();
    }
    // Удаление всех эпиков
    public void removeAllEpics() {

        // TODO:
        // Полагаем, что подзадачи не имеют смысла без эпика
        subtasks.clear();
        epics.clear();
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
        return epics.getOrDefault(id, null);
    }

    // Добавление новой задачи (обычной)
    public void addBasicTask(Task task) {
        basicTasks.put(task.getId(), task);
    }
    // Добавление новой подзадачи
    public void addSubtask(Subtask subtask) {
        // Ничего не делаем, если уже есть подзадача с таким идентификатором
        if (subtasks.containsKey(subtask.getId())) {
            return;
        }

        subtasks.put(subtask.getId(), subtask);
        Epic epic = subtask.getEpic();
        epic.addSubtask(subtask);
        TaskStatus newEpicStatus = calculateEpicStatus(epic);
        if (epic.getStatus() != newEpicStatus) {
            updateEpic(new Epic(epic.getId(), epic.getName(), epic.getDescription(), newEpicStatus,
                    epic.getAllSubtasks()));
        }
    }
    // Добавление нового эпика
    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    // Обновление задачи (обычной)
    public void updateBasicTask(Task updatedTask) {
        basicTasks.put(updatedTask.getId(), updatedTask);
    }
    // Обновление подзадачи
    public void updateSubtask(Subtask updatedSubtask) {
        subtasks.put(updatedSubtask.getId(), updatedSubtask);

        Epic epic = updatedSubtask.getEpic();

        if (epic == null) {
            return;
        }

        epic.updateSubtask(updatedSubtask);

        TaskStatus newEpicStatus = calculateEpicStatus(epic);
        if (epic.getStatus() != newEpicStatus) {
            updateEpic(new Epic(epic.getId(), epic.getName(), epic.getDescription(), newEpicStatus,
                    epic.getAllSubtasks()));
        }
    }
    // Обновление эпика
    public void updateEpic(Epic updatedEpic) {
        Epic epic = getEpicById(updatedEpic.getId());

        for (Subtask subtask : epic.getAllSubtasks()) {
            subtask.setEpic(updatedEpic);
        }

        epics.put(updatedEpic.getId(), updatedEpic);
    }

    // Расчёт статуса эпика на основе статусов его подзадач
    private TaskStatus calculateEpicStatus(Epic epic) {
        ArrayList<Subtask> epicSubtasks = epic.getAllSubtasks();

        if (epicSubtasks.isEmpty()) {
            return TaskStatus.NEW;
        } else {
            int newSubtasks = 0;
            int doneSubtasks = 0;

            for (Subtask subtask : epicSubtasks) {
                switch (subtask.getStatus()) {
                    case NEW:
                        newSubtasks++;
                        break;
                    case DONE:
                        doneSubtasks++;
                        break;
                }
            }

            if (newSubtasks == epicSubtasks.size()) {
                return TaskStatus.NEW;
            } else if (doneSubtasks == epicSubtasks.size()) {
                return TaskStatus.DONE;
            } else {
                return TaskStatus.IN_PROGRESS;
            }
        }
    }

    // Удаление задачи (обычной) по идентификатору
    public void removeBasicTaskById(int id) {
        basicTasks.remove(id);
    }
    // Удаление подзадачи по идентификатору
    public void removeSubtaskById(int id) {
        Subtask subtask = getSubtaskById(id);

        Epic epic = subtask.getEpic();
        epic.removeSubtask(subtask);
        subtasks.remove(id);

        TaskStatus newEpicStatus = calculateEpicStatus(epic);
        if (epic.getStatus() != newEpicStatus) {
            updateEpic(new Epic(epic.getId(), epic.getName(), epic.getDescription(), newEpicStatus,
                    epic.getAllSubtasks()));
        }
    }
    // Удаление эпика по идентификатору
    public void removeEpicById(int id) {
        Epic epic = getEpicById(id);

        for (Subtask subtask : epic.getAllSubtasks()) {
            updateSubtask(new Subtask(subtask.getId(), subtask.getName(), subtask.getDescription(),
                    subtask.getStatus(), null));
        }

        epics.remove(id);
    }

    // Получение всех подзадач для указанного эпика
    public ArrayList<Subtask> getAllSubtasksOfEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            return null;
        } else {
            return epic.getAllSubtasks();
        }
    }
}
