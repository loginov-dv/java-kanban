package ru.yandex.practicum.TaskManager;

import ru.yandex.practicum.Tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Класс для описания трекера задач
public class TaskManager {
    // Обычные задачи
    private final Map<Integer, Task> basicTasks;
    // Эпики
    private final Map<Integer, Epic> epics;
    // Подзадачи
    private final Map<Integer, Subtask> subtasks;
    // Последний присвоенный идентификатор
    private int globalID;

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
    public List<Task> getAllBasicTasks() {
        return new ArrayList<>(basicTasks.values());
    }
    // Получение списка всех подзадач
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }
    // Получение списка всех эпиков
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    // Удаление всех задач (обычных)
    public void removeAllBasicTasks() {
        basicTasks.clear();
    }
    // Удаление всех подзадач
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            // Удаляем все подзадачи из эпика
            epic.removeAllSubtasks();
            // Пересчитываем статус эпика и пересоздаём его только в том случае,
            // если новый статус отличается от текущего
            TaskStatus newEpicStatus = calculateEpicStatus(epic.getSubtaskIDs());
            if (newEpicStatus != epic.getStatus()) {
                updateEpic(new Epic(epic.getId(), epic.getName(), epic.getDescription(), TaskStatus.NEW));
            }
        }

        subtasks.clear();
    }
    // Удаление всех эпиков
    public void removeAllEpics() {
        for (Subtask subtask : subtasks.values()) {
            // Разрываем связь с эпиком у подзадачи
            subtask.setEpicID(null);
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
        // Добавляем id новой подзадачи в эпик
        epic.addSubtask(subtask.getId());
        // Рассчитываем новый статус
        TaskStatus newEpicStatus = calculateEpicStatus(epic.getSubtaskIDs());
        // Пересоздаём эпик только в том случае, если новый рассчитанный статус отличается от текущего
        // (по условию статус задачи обновляется вместе с полным обновлением задачи)
        if (newEpicStatus != epic.getStatus()) {
            updateEpic(new Epic(epic.getId(), epic.getName(), epic.getDescription(), newEpicStatus,
                    epic.getSubtaskIDs()));
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
        // Обновляем подзадачу в хешмапе
        subtasks.put(updatedSubtask.getId(), updatedSubtask);

        // Обновляем эпик, к которому относится подзадача
        Integer epicID = updatedSubtask.getEpicID();
        // Если epicID = null, подзадача без эпика
        if (epicID == null) {
            return;
        }

        Epic epic = getEpicById(epicID);
        // Рассчитываем новый статус
        TaskStatus newEpicStatus = calculateEpicStatus(epic.getSubtaskIDs());
        // Пересоздаём эпик только в том случае, если новый рассчитанный статус отличается от текущего
        // (по условию статус задачи обновляется вместе с полным обновлением задачи)
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
    private TaskStatus calculateEpicStatus(List<Integer> subtaskIDs) {
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
        // Если epicID = null, подзадача без эпика
        if (epicID == null) {
            return;
        }

        Epic epic = getEpicById(epicID);
        // Удаляем подзадачу из эпика
        epic.removeSubtask(id);
        // Рассчитываем новый статус
        TaskStatus newEpicStatus = calculateEpicStatus(epic.getSubtaskIDs());
        // Пересоздаём эпик только в том случае, если новый рассчитанный статус отличается от текущего
        // (по условию статус задачи обновляется вместе с полным обновлением задачи)
        if (newEpicStatus != epic.getStatus()) {
            updateEpic(new Epic(epic.getId(), epic.getName(), epic.getDescription(), newEpicStatus,
                    epic.getSubtaskIDs()));
        }
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
    public List<Subtask> getAllSubtasksOfEpic(Epic epic) {
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
