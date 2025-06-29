package ru.yandex.practicum.managers;

import ru.yandex.practicum.tasks.*;

import java.util.List;

// Интерфейс для предоставления функциональности управления историей просмотра задач
public interface HistoryManager {

    void addTask(Task task);

    List<Task> getHistory();

    void removeTask(int id);
}
