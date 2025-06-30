package ru.yandex.practicum.managers;

import ru.yandex.practicum.exceptions.TaskNotFoundException;
import ru.yandex.practicum.exceptions.TaskOverlapException;
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
        prioritySet = new TreeSet<>(Comparator.comparing(task -> task.getStartTime().orElse(null),
                Comparator.nullsFirst(Comparator.naturalOrder())));
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
        basicTasks.keySet().forEach(historyManager::removeTask);
        // Удаляем все задачи из множества
        prioritySet.removeAll(basicTasks.values());
        // Удаляем все задачи из трекера
        basicTasks.clear();
    }

    // Удаление всех подзадач
    @Override
    public void removeAllSubtasks() {
        // Пересоздаём все эпики с пустыми списками id подзадач и статусом NEW,
        // startTime будет null, а duration = 0
        epics.values().forEach(epic ->
                updateEpic(new Epic(epic.getID(), epic.getName(), epic.getDescription())));
        // Удаляем все подзадачи из истории
        subtasks.keySet().forEach(historyManager::removeTask);
        // Удаляем все подзадачи из множества
        prioritySet.removeAll(subtasks.values());
        // Удаляем все подзадачи из трекера
        subtasks.clear();
    }

    // Удаление всех эпиков
    @Override
    public void removeAllEpics() {
        // Удаляем все эпики из истории
        epics.keySet().forEach(historyManager::removeTask);
        // Удаляем все эпики из трекера
        epics.clear();
        // Удаляем все подзадачи из истории
        subtasks.keySet().forEach(historyManager::removeTask);
        // Удаляем все подзадачи из множества
        prioritySet.removeAll(subtasks.values());
        // Удаляем все подзадачи из трекера
        subtasks.clear();
    }

    // Получение задачи (обычной) по id
    @Override
    public Task getBasicTaskById(int id) throws TaskNotFoundException {
        if (!basicTasks.containsKey(id)) {
            throw new TaskNotFoundException("Задача с id = " + id + " не найдена");
        }

        // Добавляем задачу в историю просмотра
        addToHistory(basicTasks.get(id));

        return basicTasks.get(id);
    }

    // Получение подзадачи по id
    @Override
    public Subtask getSubtaskById(int id) throws TaskNotFoundException {
        if (!subtasks.containsKey(id)) {
            throw new TaskNotFoundException("Подзадача с id = " + id + " не найдена");
        }

        // Добавляем подзадачу в историю просмотра
        addToHistory(subtasks.get(id));

        return subtasks.get(id);
    }

    // Получение эпика по id
    @Override
    public Epic getEpicById(int id) throws TaskNotFoundException {
        if (!epics.containsKey(id)) {
            throw new TaskNotFoundException("Эпик с id = " + id + " не найден");
        }

        // Добавляем эпик в историю просмотра
        addToHistory(epics.get(id));

        return epics.get(id);
    }

    // Добавление новой задачи (обычной)
    @Override
    public void addBasicTask(Task task) throws TaskOverlapException {
        // Ничего не делаем, если уже есть задача с таким идентификатором
        if (basicTasks.containsKey(task.getID())) {
            return;
        }

        // При добавлении задачи проверяем наличие даты и времени начала
        if (task.getStartTime().isPresent()) {
            // Если параметр задан, то добавляем задачу в мапу (и в множество) только если
            // она не пересекается по времени выполнения с другими задачами
            if (getPrioritizedTasks().stream().anyMatch(task::hasOverlapWith)) {
                throw new TaskOverlapException("Задача имеет пересечение по времени выполнения с другой задачей");
            }

            basicTasks.put(task.getID(), task);
            prioritySet.add(task);
        } else {
            // Если параметр не задан, то добавляем задачу только в мапу
            basicTasks.put(task.getID(), task);
        }

        // Изменяем globalID для корректного присвоения идентификаторов новым задачам
        if (task.getID() > globalID) {
            globalID = task.getID();
        }
    }

    // Добавление новой подзадачи
    @Override
    public void addSubtask(Subtask subtask) throws TaskOverlapException {
        // Ничего не делаем, если уже есть подзадача с таким идентификатором
        if (subtasks.containsKey(subtask.getID())) {
            return;
        }

        // При добавлении подзадачи проверяем наличие даты и времени начала
        if (subtask.getStartTime().isPresent()) {
            // Если параметр задан, то добавляем подзадачу в мапу (и в множество) только если
            // она не пересекается по времени выполнения с другими задачами
            if (getPrioritizedTasks().stream().anyMatch(subtask::hasOverlapWith)) {
                throw new TaskOverlapException("Задача имеет пересечение по времени выполнения с другой задачей");
            }

            subtasks.put(subtask.getID(), subtask);
            prioritySet.add(subtask);
        } else {
            // Если параметр не задан, то добавляем подзадачу только в мапу
            subtasks.put(subtask.getID(), subtask);
        }

        // Изменяем globalID для корректного присвоения идентификаторов новым задачам
        if (subtask.getID() > globalID) {
            globalID = subtask.getID();
        }

        // Обновляем эпик, к которому относится подзадача
        if (subtask.getEpicID() == null) {
            return;
            // TODO: мб исключение?
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
        // Рассчитываем новую дату начала
        LocalDateTime newStartTime = calculateEpicStartTime(epicSubtasks);
        // Рассчитываем новую продолжительность
        Duration newDuration = calculateEpicDuration(epicSubtasks);
        // Рассчитываем новую дату окончания
        LocalDateTime newEndTime = calculateEpicEndTime(epicSubtasks);

        // Пересоздаём эпик
        updateEpic(new Epic(epic.getID(), epic.getName(), epic.getDescription(), newEpicStatus, epicSubtasks,
                newStartTime, newDuration, newEndTime));
    }

    // Добавление нового эпика
    @Override
    public void addEpic(Epic epic) {
        epics.put(epic.getID(), epic);

        // Изменяем globalID для корректного присвоения идентификаторов новым задачам
        if (epic.getID() > globalID) {
            globalID = epic.getID();
        }
    }

    // Обновление задачи (обычной)
    @Override
    public void updateBasicTask(Task updatedTask) throws TaskOverlapException, TaskNotFoundException {
        if (!basicTasks.containsKey(updatedTask.getID())) {
            throw new TaskNotFoundException("Задача с id = " + updatedTask.getID() + " не найдена");
        }

        // При обновлении задачи проверяем наличие даты и времени начала
        if (updatedTask.getStartTime().isPresent()) {
            // Если параметр задан, то обновляем задачу только если
            // она не пересекается по времени выполнения с другими задачами
            if (getPrioritizedTasks().stream()
                    .filter(task -> !task.equals(updatedTask))
                    .anyMatch(updatedTask::hasOverlapWith)) {
                throw new TaskOverlapException("Задача имеет пересечение по времени выполнения с другой задачей");
            }

            basicTasks.put(updatedTask.getID(), updatedTask);
            // Обновляем в множестве
            prioritySet.remove(updatedTask);
            if (updatedTask.getStartTime().isPresent()) {
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
    public void updateSubtask(Subtask updatedSubtask) throws TaskOverlapException, TaskNotFoundException {
        if (!subtasks.containsKey(updatedSubtask.getID())) {
            throw new TaskNotFoundException("Подзадача с id = " + updatedSubtask.getID() + " не найдена");
        }

        // При обновлении подзадачи проверяем наличие даты и времени начала
        if (updatedSubtask.getStartTime().isPresent()) {
            // Если параметр задан, то обновляем подзадачу только если
            // она не пересекается по времени выполнения с другими задачами
            if (getPrioritizedTasks().stream()
                    .filter(task -> !task.equals(updatedSubtask))
                    .anyMatch(updatedSubtask::hasOverlapWith)) {
                throw new TaskOverlapException("Задача имеет пересечение по времени выполнения с другой задачей");
            }

            subtasks.put(updatedSubtask.getID(), updatedSubtask);
            // Обновляем в множестве
            prioritySet.remove(updatedSubtask);
            if (updatedSubtask.getStartTime().isPresent()) {
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
        // Рассчитываем новую дату начала
        LocalDateTime newStartTime = calculateEpicStartTime(epic.getSubtaskIDs());
        // Рассчитываем новую продолжительность
        Duration newDuration = calculateEpicDuration(epic.getSubtaskIDs());
        // Рассчитываем новую дату окончания
        LocalDateTime newEndTime = calculateEpicEndTime(epic.getSubtaskIDs());

        // Пересоздаём эпик
        updateEpic(new Epic(epic.getID(), epic.getName(), epic.getDescription(), newEpicStatus,
                epic.getSubtaskIDs(), newStartTime, newDuration, newEndTime));
    }

    // Обновление эпика
    @Override
    public void updateEpic(Epic updatedEpic) throws TaskNotFoundException {
        if (!epics.containsKey(updatedEpic.getID())) {
            throw new TaskNotFoundException("Эпик с id = " + updatedEpic.getID() + " не найден");
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
        // Рассчитываем новую дату начала
        LocalDateTime newStartTime = calculateEpicStartTime(epicSubtasks);
        // Рассчитываем новую продолжительность
        Duration newDuration = calculateEpicDuration(epicSubtasks);
        // Рассчитываем новую дату окончания
        LocalDateTime newEndTime = calculateEpicEndTime(epicSubtasks);

        // Пересоздаём эпик
        updateEpic(new Epic(epic.getID(), epic.getName(), epic.getDescription(), newEpicStatus, epicSubtasks,
                newStartTime, newDuration, newEndTime));

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
        // Если подзадачи отсутствуют
        if (subtaskIDs.isEmpty()) {
            return Duration.ZERO;
        }
        // Иначе рассчитываем
        return subtaskIDs.stream()
                .map(subtaskId -> subtasks.get(subtaskId).getDuration())
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
    }

    // Расчёт времени начала эпика, которое равно дате старта самой ранней подзадачи
    private LocalDateTime calculateEpicStartTime(List<Integer> subtaskIDs) {
        // Если подзадачи отсутствуют
        if (subtaskIDs.isEmpty()) {
            return null;
        }
        // Иначе рассчитываем
        return subtaskIDs.stream()
                .map(subtaskId -> subtasks.get(subtaskId).getStartTime())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    // Расчёт времени конца эпика, которое равно дате окончания самой поздней подзадачи
    private LocalDateTime calculateEpicEndTime(List<Integer> subtaskIDs) {
        // Если подзадачи отсутствуют
        if (subtaskIDs.isEmpty()) {
            return null;
        }
        // Иначе рассчитываем
        return subtaskIDs.stream()
                .map(subtaskId -> subtasks.get(subtaskId).getEndTime())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    // Возвращает список задач и подзадач в порядке приоритета (от более ранней даты начала к более поздней)
    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritySet);
    }
}
