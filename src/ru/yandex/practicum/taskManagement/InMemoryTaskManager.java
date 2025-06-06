package ru.yandex.practicum.taskManagement;

import ru.yandex.practicum.tasks.*;

import java.util.*;

// Класс для описания трекера задач
public class InMemoryTaskManager implements TaskManager {
    // Обычные задачи
    private final Map<Integer, Task> basicTasks;
    // Эпики
    private final Map<Integer, Epic> epics;
    // Подзадачи
    private final Map<Integer, Subtask> subtasks;

    // Последний присвоенный id
    private int globalID;

    // Менеджер истории просмотра задач
    private final HistoryManager historyManager;

    // Конструктор класса InMemoryTaskManager
    public InMemoryTaskManager(HistoryManager historyManager) {
        basicTasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        globalID = 0;
        this.historyManager = historyManager;
    }

    // Конструктор класса InMemoryTaskManager по умолчанию
    public InMemoryTaskManager() {
        this(new InMemoryHistoryManager());
    }

    // Получение нового id
    @Override
    public int nextId() {
        return ++globalID;
    }

    // Получение списка всех задач (обычных)
    @Override
    public List<Task> getAllBasicTasks() {
        return new ArrayList<>(basicTasks.values());
    }

    // Получение списка всех подзадач
    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // Получение списка всех эпиков
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    // Удаление всех задач (обычных)
    @Override
    public void removeAllBasicTasks() {
        // Удаляем задачи из истории
        for (Integer taskId : basicTasks.keySet()) {
            historyManager.removeTask(taskId);
        }

        // Удаляем задачи из трекера
        basicTasks.clear();
    }

    // Удаление всех подзадач
    @Override
    public void removeAllSubtasks() {
        // Пересоздаём все эпики с пустыми списками id подзадач и статусом NEW
        for (Epic epic : epics.values()) {
            updateEpic(new Epic(epic.getID(), epic.getName(), epic.getDescription(), TaskStatus.NEW));
        }

        // Удаляем все подзадачи из истории
        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.removeTask(subtaskId);
        }

        // Удаляем все подзадачи из трекера
        subtasks.clear();
    }

    // Удаление всех эпиков
    @Override
    public void removeAllEpics() {
        // Удаляем все эпики из истории
        for (Integer epicId : epics.keySet()) {
            historyManager.removeTask(epicId);
        }

        // Удаляем все эпики из трекера
        epics.clear();

        // Удаляем все подзадачи из истории
        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.removeTask(subtaskId);
        }

        // Удаляем все подзадачи из трекера
        subtasks.clear();
    }

    // Получение задачи (обычной) по id
    @Override
    public Task getBasicTaskById(int id) {
        // Добавляем задачу в историю просмотра
        addToHistory(basicTasks.get(id));

        return basicTasks.get(id);
    }

    // Получение подзадачи по id
    @Override
    public Subtask getSubtaskById(int id) {
        // Добавляем подзадачу в историю просмотра
        addToHistory(subtasks.get(id));

        return subtasks.get(id);
    }

    // Получение эпика по id
    @Override
    public Epic getEpicById(int id) {
        // Добавляем эпик в историю просмотра
        addToHistory(epics.get(id));

        return epics.get(id);
    }

    // Добавление новой задачи (обычной)
    @Override
    public void addBasicTask(Task task) {
        basicTasks.put(task.getID(), task);
    }

    // Добавление новой подзадачи
    @Override
    public void addSubtask(Subtask subtask) {
        // Ничего не делаем, если уже есть подзадача с таким идентификатором
        if (subtasks.containsKey(subtask.getID())) {
            return;
        }

        // Добавляем новую подзадачу в хешмапу
        subtasks.put(subtask.getID(), subtask);

        // Обновляем эпик, к которому относится подзадача
        if (subtask.getEpicID() == null) {
            return;
        }

        Epic epic = epics.get(subtask.getEpicID());

        // Проверка на отсутствие эпика в трекере
        if (epic == null) {
            return;
        }

        // Добавляем id новой подзадачи в эпик
        List<Integer> epicSubtasks = new ArrayList<>(epic.getSubtaskIDs());
        epicSubtasks.add(subtask.getID());

        // Рассчитываем новый статус
        TaskStatus newEpicStatus = calculateEpicStatus(epicSubtasks);

        // Пересоздаём эпик
        updateEpic(new Epic(epic.getID(), epic.getName(), epic.getDescription(), newEpicStatus, epicSubtasks));
    }

    // Добавление нового эпика
    @Override
    public void addEpic(Epic epic) {
        epics.put(epic.getID(), epic);
    }

    // Обновление задачи (обычной)
    @Override
    public void updateBasicTask(Task updatedTask) {
        basicTasks.put(updatedTask.getID(), updatedTask);
    }

    // Обновление подзадачи
    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        // Обновляем подзадачу в хешмапе
        subtasks.put(updatedSubtask.getID(), updatedSubtask);

        // Обновляем эпик, к которому относится подзадача
        if (updatedSubtask.getEpicID() == null) {
            return;
        }

        Epic epic = epics.get(updatedSubtask.getEpicID());

        // Рассчитываем новый статус
        TaskStatus newEpicStatus = calculateEpicStatus(epic.getSubtaskIDs());

        // Пересоздаём эпик, если новый рассчитанный статус отличается от текущего (в иных случаях не требуется)
        if (epic.getStatus() != newEpicStatus) {
            updateEpic(new Epic(epic.getID(), epic.getName(), epic.getDescription(), newEpicStatus,
                    epic.getSubtaskIDs()));
        }
    }

    // Обновление эпика
    @Override
    public void updateEpic(Epic updatedEpic) {
        epics.put(updatedEpic.getID(), updatedEpic);
    }

    // Расчёт статуса эпика на основе статусов его подзадач
    private TaskStatus calculateEpicStatus(List<Integer> subtaskIDs) {
        // Подзадачи, относящиеся к эпику
        List<Subtask> epicSubtasks = new ArrayList<>();
        for (Integer subtaskID : subtaskIDs) {
            epicSubtasks.add(subtasks.get(subtaskID));
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

    // Удаление задачи (обычной) по id
    @Override
    public void removeBasicTaskById(int id) {
        // Удаляем из трекера
        basicTasks.remove(id);

        // Удаляем из истории
        historyManager.removeTask(id);
    }

    // Удаление подзадачи по id (с пересчётом статуса эпика и его пересозданием при необходимости)
    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);

        // Обновляем эпик
        if (subtask.getEpicID() == null) {
            return;
        }

        Epic epic = epics.get(subtask.getEpicID());

        // Удаляем подзадачу из эпика
        List<Integer> epicSubtasks = new ArrayList<>(epic.getSubtaskIDs());
        epicSubtasks.remove(Integer.valueOf(id));

        // Рассчитываем новый статус
        TaskStatus newEpicStatus = calculateEpicStatus(epicSubtasks);

        // Пересоздаём эпик
        updateEpic(new Epic(epic.getID(), epic.getName(), epic.getDescription(), newEpicStatus, epicSubtasks));

        // Удаляем подзадачу из трекера
        subtasks.remove(id);

        // Удаляем подзадачу из истории
        historyManager.removeTask(id);
    }

    // Удаление эпика по id
    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);

        // Удаляем подзадачи эпика
        for (Subtask subtask : getAllEpicSubtasks(epic)) {
            subtasks.remove(subtask.getID());
            historyManager.removeTask(subtask.getID());
        }

        // Удаляем эпик из трекера
        epics.remove(id);

        // Удаляем эпик из истории
        historyManager.removeTask(id);
    }

    // Получение всех подзадач для указанного эпика
    @Override
    public List<Subtask> getAllEpicSubtasks(Epic epic) {
        if (!epics.containsKey(epic.getID())) {
            return Collections.emptyList();
        } else {
            List<Subtask> epicSubtasks = new ArrayList<>();
            for (Integer subtaskID : epic.getSubtaskIDs()) {
                epicSubtasks.add(subtasks.get(subtaskID));
            }

            return epicSubtasks;
        }
    }

    // Получить список просмотренных задач
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Добавить задачу в историю просмотра
    private void addToHistory(Task task) {
        historyManager.addTask(task);
    }
}
