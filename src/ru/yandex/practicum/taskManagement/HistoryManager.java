package ru.yandex.practicum.taskManagement;

import ru.yandex.practicum.tasks.*;

import java.util.List;

// Интерфейс для предоставления функциональности истории просмотренных задач
public interface HistoryManager {

    void addTask(Task task);

    List<Task> getHistory();
}
