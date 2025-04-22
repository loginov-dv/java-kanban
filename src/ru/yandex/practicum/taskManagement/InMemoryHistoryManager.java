package ru.yandex.practicum.taskManagement;

import ru.yandex.practicum.tasks.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    @Override
    public void addTask(Task task) {

    }

    @Override
    public List<Task> getHistory() {
        return List.of();
    }
}
