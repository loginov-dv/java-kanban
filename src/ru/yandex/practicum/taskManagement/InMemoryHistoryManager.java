package ru.yandex.practicum.taskManagement;

import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.List;

// Класс для управления историей просмотра задач
public class InMemoryHistoryManager implements HistoryManager {

    // Список просмотренных задач
    private final ArrayList<Task> history;
    // Количество задач в истории просмотра
    private final int depth;

    // Количество задач в истории просмотра по умолчанию
    private static final int DEFAULT_DEPTH = 10;

    // Конструктор класса InMemoryHistoryManager
    public InMemoryHistoryManager(int depth) {
        this.depth = depth;
        history = new ArrayList<>();
    }

    // Конструктор класса InMemoryHistoryManager
    public InMemoryHistoryManager() {
        this(DEFAULT_DEPTH);
    }

    // Добавить задачу в список
    @Override
    public void addTask(Task task) {
        if (history.size() == depth) {
            history.removeFirst();
        }

        history.add(task);
    }

    // Вернуть список просмотренных задач
    @Override
    public List<Task> getHistory() {
        if (history.isEmpty()) {
            return List.of();
        }

        return history;
    }
}
