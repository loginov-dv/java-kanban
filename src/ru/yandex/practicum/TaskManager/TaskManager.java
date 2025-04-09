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
    private static int globalID;

    // Конструктор класса TaskManager
    public TaskManager() {
        basicTasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        globalID = 0;
    }

    // Получение нового идентификатора
    public int nextId() {
        return ++globalID;
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
        for (Epic epic : epics.values()) {
            updateEpic(new Epic(epic.getId(), epic.getName(), epic.getDescription(), TaskStatus.NEW));
        }

        subtasks.clear();
    }
    // Удаление всех эпиков
    public void removeAllEpics() {
        for (Subtask subtask : subtasks.values()) {
            updateSubtask(new Subtask(subtask.getId(), subtask.getName(), subtask.getDescription(), subtask.getStatus(), null));
        }

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
        // Добавляем новую подзадачу в хешмапу
        subtasks.put(subtask.getId(), subtask);

        // Обновляем эпик, к которому относится подзадача
        Integer epicID = subtask.getEpicID();
        // Если epicID = null, подзадача без эпика
        if (epicID == null) {
            return;
        }
        Epic epic = getEpicById(epicID);
        // Формируем новый список идентификаторов подзадач эпика (с учётом новой подзадачи)
        ArrayList<Integer> newSubtaskIDs = new ArrayList<>(epic.getSubtaskIDs());
        newSubtaskIDs.add(subtask.getId());
        // Определяем новый статус и обновляем эпик
        TaskStatus newEpicStatus = calculateEpicStatus(newSubtaskIDs);
        updateEpic(new Epic(epic.getId(), epic.getName(), epic.getDescription(), newEpicStatus, newSubtaskIDs));
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
        // Обновляем подзадачу в хешмапе
        subtasks.put(updatedSubtask.getId(), updatedSubtask);

        // Определяем новый статус эпика и обновляем, при необходимости
        Integer epicID = updatedSubtask.getEpicID();
        if (epicID == null) {
            return;
        }
        Epic epic = getEpicById(epicID);
        TaskStatus newEpicStatus = calculateEpicStatus(epic.getSubtaskIDs());
        if (epic.getStatus() != newEpicStatus) {
            updateEpic(new Epic(epic.getId(), epic.getName(), epic.getDescription(), newEpicStatus,
                    epic.getSubtaskIDs()));
        }
    }
    // Обновление эпика
    public void updateEpic(Epic updatedEpic) {
        epics.put(updatedEpic.getId(), updatedEpic);
    }

    // Расчёт статуса эпика на основе статусов его подзадач
    private TaskStatus calculateEpicStatus(ArrayList<Integer> subtaskIDs) {
        // Подзадачи, относящиеся к эпику
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (Integer subtaskID : subtaskIDs) {
            epicSubtasks.add(getSubtaskById(subtaskID));
        }

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
        subtasks.remove(id);

        // Обновляем эпик
        Integer epicID = subtask.getEpicID();
        if (epicID == null) {
            return;
        }
        Epic epic = getEpicById(epicID);
        // Формируем новый список идентификаторов подзадач эпика не учитываем удалённую подзадачу)
        ArrayList<Integer> newSubtasks = new ArrayList<>(epic.getSubtaskIDs());
        newSubtasks.remove(Integer.valueOf(subtask.getId()));
        TaskStatus newEpicStatus = calculateEpicStatus(newSubtasks);
        updateEpic(new Epic(epic.getId(), epic.getName(), epic.getDescription(), newEpicStatus, newSubtasks));
    }
    // Удаление эпика по идентификатору
    public void removeEpicById(int id) {
        Epic epic = getEpicById(id);
        // Обновляем подзадачи (теперь без эпика)
        for (Subtask subtask : getAllSubtasksOfEpic(epic)) {
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
            ArrayList<Subtask> epicSubtasks = new ArrayList<>();
            for (Integer subtaskID : epic.getSubtaskIDs()) {
                epicSubtasks.add(getSubtaskById(subtaskID));
            }

            return epicSubtasks;
        }
    }
}
