package ru.yandex.practicum.taskManager;

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
    // Последний присвоенный идентификатор
    private int globalID;
    // Очередь из идентификаторов просмотренных задач
    private final Queue<Integer> history;
    // Константа, обозначающая наибольшее возможное количество задач в истории просмотра
    private static final int HISTORY_DEPTH = 10;

    // Конструктор класса TaskManager
    public InMemoryTaskManager() {
        basicTasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        globalID = 0;

        history = new ArrayDeque<>();
    }

    // Получение нового идентификатора
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
        basicTasks.clear();
    }
    // Удаление всех подзадач
    @Override
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            // Удаляем все подзадачи из эпика
            epic.removeAllSubtasks();
            // При отсутствии задач статус эпика должен быть NEW
            // Пересоздаём эпик, если текущий статус не равен NEW
            if (epic.getStatus() != TaskStatus.NEW) {
                updateEpic(new Epic(epic.getId(), epic.getName(), epic.getDescription(), TaskStatus.NEW));
            }
        }

        subtasks.clear();
    }
    // Удаление всех эпиков
    @Override
    public void removeAllEpics() {
        for (Subtask subtask : subtasks.values()) {
            // Разрываем связь с эпиком у подзадачи
            subtask.setEpicID(null);
        }

        epics.clear();
    }

    // Получение задачи (обычной) по идентификатору
    @Override
    public Task getBasicTaskById(int id) {
        addToHistory(id);
        return basicTasks.getOrDefault(id, null);
    }
    // Получение подзадачи по идентификатору
    @Override
    public Subtask getSubtaskById(int id) {
        addToHistory(id);
        return subtasks.getOrDefault(id, null);
    }
    // Получение эпика по идентификатору
    @Override
    public Epic getEpicById(int id) {
        addToHistory(id);
        return epics.getOrDefault(id, null);
    }

    // Добавление новой задачи (обычной)
    @Override
    public void addBasicTask(Task task) {
        basicTasks.put(task.getId(), task);
    }
    // Добавление новой подзадачи
    @Override
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

        Epic epic = epics.get(epicID);
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
    @Override
    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    // Обновление задачи (обычной)
    @Override
    public void updateBasicTask(Task updatedTask) {
        basicTasks.put(updatedTask.getId(), updatedTask);
    }
    // Обновление подзадачи
    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        // Обновляем подзадачу в хешмапе
        subtasks.put(updatedSubtask.getId(), updatedSubtask);

        // Обновляем эпик, к которому относится подзадача
        Integer epicID = updatedSubtask.getEpicID();
        // Если epicID = null, подзадача без эпика
        if (epicID == null) {
            return;
        }

        Epic epic = epics.get(epicID);
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
    @Override
    public void updateEpic(Epic updatedEpic) {
        epics.put(updatedEpic.getId(), updatedEpic);
    }

    // Расчёт статуса эпика на основе статусов его подзадач
    private TaskStatus calculateEpicStatus(List<Integer> subtaskIDs) {
        // Подзадачи, относящиеся к эпику
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
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

    // Удаление задачи (обычной) по идентификатору
    @Override
    public void removeBasicTaskById(int id) {
        basicTasks.remove(id);
    }
    // Удаление подзадачи по идентификатору
    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        subtasks.remove(id);

        // Обновляем эпик
        Integer epicID = subtask.getEpicID();
        // Если epicID = null, подзадача без эпика
        if (epicID == null) {
            return;
        }

        Epic epic = epics.get(epicID);
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
    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);
        // Обновляем подзадачи (теперь без эпика)
        for (Subtask subtask : getAllSubtasksOfEpic(epic)) {
            updateSubtask(new Subtask(subtask.getId(), subtask.getName(), subtask.getDescription(),
                    subtask.getStatus(), null));
        }

        epics.remove(id);
    }

    // Получение всех подзадач для указанного эпика
    @Override
    public List<Subtask> getAllSubtasksOfEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            return null;
        } else {
            ArrayList<Subtask> epicSubtasks = new ArrayList<>();
            for (Integer subtaskID : epic.getSubtaskIDs()) {
                epicSubtasks.add(subtasks.get(subtaskID));
            }

            return epicSubtasks;
        }
    }

    // Получить 10 последних просмотренных задач
    @Override
    public List<Task> getHistory() {
        // Если история просмотра пуста - возвращаем пустой список
        if (history.isEmpty()) {
            return List.of();
        }

        ArrayList<Task> viewedTasks = new ArrayList<>();

        for (Integer id : history) {
            if (basicTasks.containsKey(id)) {
                viewedTasks.add(basicTasks.get(id));
            } else if (epics.containsKey(id)) {
                viewedTasks.add(epics.get(id));
            } else viewedTasks.add(subtasks.getOrDefault(id, null));
        }

        return viewedTasks;
    }

    // Добавить идентификатор задачи в историю просмотра
    private void addToHistory(Integer id) {
        // Если очередь заполнена, освобождаем место путём удаления элемента из начала очереди
        if (history.size() == HISTORY_DEPTH) {
            history.poll();
        }

        history.add(id);
    }
}
