package ru.yandex.practicum.taskManagement;

import ru.yandex.practicum.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

// Класс для описания трекера задач
public class InMemoryTaskManager implements TaskManager {
    // Обычные задачи
    private final Map<Integer, Task> basicTasks;
    // Эпики
    private final Map<Integer, Epic> epics;
    // Подзадачи
    private final Map<Integer, Subtask> subtasks;

    // Последний присвоенный id
    protected int globalID;

    // Менеджер истории просмотра задач
    private final HistoryManager historyManager;

    // Приоритезированное по дате начала множество задач и подзадач
    private final Set<Task> prioritySet;

    // Конструктор класса InMemoryTaskManager
    public InMemoryTaskManager(HistoryManager historyManager) {
        basicTasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        globalID = 0;
        prioritySet = new TreeSet<>(Comparator.comparing(Task::getStartTime));
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
        // Удаляем все задачи из истории
        basicTasks.keySet().forEach(taskId -> historyManager.removeTask(taskId));
        // Удаляем все задачи из множества
        prioritySet.removeAll(basicTasks.values());
        // Удаляем все задачи из трекера
        basicTasks.clear();
    }

    // Удаление всех подзадач
    @Override
    public void removeAllSubtasks() {
        // Пересоздаём все эпики с пустыми списками id подзадач и статусом NEW
        epics.values().forEach(epic ->
                updateEpic(new Epic(epic.getID(), epic.getName(), epic.getDescription(), TaskStatus.NEW)));
        // Удаляем все подзадачи из истории
        subtasks.keySet().forEach(subtaskId -> historyManager.removeTask(subtaskId));
        // Удаляем все подзадачи из множества
        prioritySet.removeAll(subtasks.values());
        // Удаляем все подзадачи из трекера
        subtasks.clear();
    }

    // Удаление всех эпиков
    @Override
    public void removeAllEpics() {
        // Удаляем все эпики из истории
        epics.keySet().forEach(epicId -> historyManager.removeTask(epicId));
        // Удаляем все эпики из трекера
        epics.clear();
        // Удаляем все подзадачи из истории
        subtasks.keySet().forEach(subtaskId -> historyManager.removeTask(subtaskId));
        // Удаляем все подзадачи из множества
        prioritySet.removeAll(subtasks.values());
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
        // Ничего не делаем, если уже есть задача с таким идентификатором
        if (basicTasks.containsKey(task.getID())) {
            return;
        }

        // При добавлении задачи проверяем наличие даты и времени начала
        if (task.getStartTime() != null) {
            // Если параметр задан, то добавляем задачу в мапу (и в множество) только если
            // она не пересекается по времени выполнения с другими задачами
            if (getPrioritizedTasks().stream().anyMatch(task::hasIntersectionWith)) {
                // TODO: исключение?
                return;
            }

            basicTasks.put(task.getID(), task);
            prioritySet.add(task);
        } else {
            // Если параметр не задан, то добавляем задачу только в мапу
            basicTasks.put(task.getID(), task);
        }
    }

    // Добавление новой подзадачи
    @Override
    public void addSubtask(Subtask subtask) {
        // Ничего не делаем, если уже есть подзадача с таким идентификатором
        if (subtasks.containsKey(subtask.getID())) {
            return;
        }

        // При добавлении подзадачи проверяем наличие даты и времени начала
        if (subtask.getStartTime() != null) {
            // Если параметр задан, то добавляем подзадачу в мапу (и в множество) только если
            // она не пересекается по времени выполнения с другими задачами
            if (getPrioritizedTasks().stream().anyMatch(subtask::hasIntersectionWith)) {
                return;
            }

            subtasks.put(subtask.getID(), subtask);
            prioritySet.add(subtask);
        } else {
            // Если параметр не задан, то добавляем подзадачу только в мапу
            subtasks.put(subtask.getID(), subtask);
        }

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
        // Ничего не делаем, если нет задачи с таким идентификатором
        if (!subtasks.containsKey(updatedTask.getID())) {
            return;
        }

        // При обновлении задачи проверяем наличие даты и времени начала
        if (updatedTask.getStartTime() != null) {
            // Если параметр задан, то обновляем задачу только если
            // она не пересекается по времени выполнения с другими задачами
            if (getPrioritizedTasks().stream().anyMatch(updatedTask::hasIntersectionWith)) {
                // TODO: исключение?
                return;
            }

            basicTasks.put(updatedTask.getID(), updatedTask);
            // Обновляем в множестве
            prioritySet.remove(updatedTask);
            if (updatedTask.getStartTime() != null) {
                prioritySet.add(updatedTask);
            }
        } else {
            // Если параметр не задан, то обновляем в мапе и удаляем из множества
            basicTasks.put(updatedTask.getID(), updatedTask);
            prioritySet.remove(updatedTask);
        }
    }

    // Обновление подзадачи
    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        // Ничего не делаем, если нет подзадачи с таким идентификатором
        if (!subtasks.containsKey(updatedSubtask.getID())) {
            return;
        }

        // При обновлении подзадачи проверяем наличие даты и времени начала
        if (updatedSubtask.getStartTime() != null) {
            // Если параметр задан, то обновляем подзадачу только если
            // она не пересекается по времени выполнения с другими задачами
            if (getPrioritizedTasks().stream().anyMatch(updatedSubtask::hasIntersectionWith)) {
                // TODO: исключение?
                return;
            }

            subtasks.put(updatedSubtask.getID(), updatedSubtask);
            // Обновляем в множестве
            prioritySet.remove(updatedSubtask);
            if (updatedSubtask.getStartTime() != null) {
                prioritySet.add(updatedSubtask);
            }
        } else {
            // Если параметр не задан, то обновляем в мапе и удаляем из множества
            subtasks.put(updatedSubtask.getID(), updatedSubtask);
            prioritySet.remove(updatedSubtask);
        }

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
        // Ничего не делаем, если нет эпика с таким идентификатором
        if (!epics.containsKey(updatedEpic.getID())) {
            return;
        }

        epics.put(updatedEpic.getID(), updatedEpic);
    }

    // Расчёт статуса эпика на основе статусов его подзадач
    private TaskStatus calculateEpicStatus(List<Integer> subtaskIDs) {
        // Подзадачи, относящиеся к эпику
        List<Subtask> epicSubtasks = subtaskIDs.stream()
                .map(subtaskId -> subtasks.get(subtaskId))
                .collect(Collectors.toList());

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
        // Удаляем задачу из множества
        prioritySet.remove(basicTasks.get(id));
        // Удаляем задачу из трекера
        basicTasks.remove(id);
        // Удаляем задачу из истории
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

        // Удаляем подзадачу из множества
        prioritySet.remove(subtasks.get(id));
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
        getAllEpicSubtasks(epic).forEach(subtask -> {
            prioritySet.remove(subtask);
            subtasks.remove(subtask.getID());
            historyManager.removeTask(subtask.getID());
        });

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
            return epic.getSubtaskIDs().stream()
                    .map(subtaskId -> subtasks.get(subtaskId))
                    .collect(Collectors.toList());
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

    // Расчёт продолжительности эпика, которая равна сумме продолжительностей его подзадач
    private Duration calculateEpicDuration(List<Integer> subtaskIDs) {
        return subtaskIDs.stream()
                .map(subtaskId -> subtasks.get(subtaskId).getDuration())
                .reduce(Duration.ZERO, Duration::plus);
    }

    // Расчёт времени начала эпика, которое равно дате старта самой ранней подзадачи
    private LocalDateTime calculateEpicStartTime(List<Integer> subtasksIDs) {
        return subtasksIDs.stream()
                .map(subtaskId -> subtasks.get(subtaskId).getStartTime())
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    // Расчёт времени конца эпика, которое равно дате окончания самой поздней подзадачи
    private LocalDateTime calculateEpicEndTime(List<Integer> subtasksIDs) {
        return subtasksIDs.stream()
                .map(subtaskId -> subtasks.get(subtaskId).getStartTime())
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    // Возвращает список задач и подзадач в порядке приоритета (от более ранней даты начала к более поздней)
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritySet);
    }
}
