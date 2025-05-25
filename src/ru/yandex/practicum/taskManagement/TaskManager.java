package ru.yandex.practicum.taskManagement;

import ru.yandex.practicum.tasks.*;

import java.util.List;

// Интерфейс для предоставления функциональности трекера задач
public interface TaskManager {

    List<Task> getAllBasicTasks();

    List<Subtask> getAllSubtasks();

    List<Epic> getAllEpics();

    void removeAllBasicTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    Task getBasicTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    void addBasicTask(Task task);

    void addSubtask(Subtask subtask);

    void addEpic(Epic epic);

    void updateBasicTask(Task updatedTask);

    void updateSubtask(Subtask updatedSubtask);

    void updateEpic(Epic updatedEpic);

    void removeBasicTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicById(int id);

    List<Subtask> getAllEpicSubtasks(Epic epic);

    List<Task> getHistory();

    int nextId();
}
