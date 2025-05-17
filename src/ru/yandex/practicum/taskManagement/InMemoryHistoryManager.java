package ru.yandex.practicum.taskManagement;

import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.List;

// Класс для управления историей просмотра задач
public class InMemoryHistoryManager implements HistoryManager {

    // Список просмотренных задач
    private final ArrayList<Task> history;
    // Размер списка задач
    private static final int MAX_SIZE = 10;

    // Конструктор класса InMemoryHistoryManager
    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    // Добавить задачу в список
    @Override
    public void addTask(Task task) {
        // Проверка на null
        if (task == null) {
            return;
        }
        // Удаляем первый элемент в списке, если размер списка исчерпан
        if (history.size() == MAX_SIZE) {
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
        // Возвращаем новый список
        return new ArrayList<>(history);
    }

    // Удалить все вхождения задачи с указанным id из истории
    @Override
    public void removeTask(int id) {
        List<Task> tasksToRemove = new ArrayList<>();

        for (Task task : history) {
            if (task.getID() == id) {
                tasksToRemove.add(task);
            }
        }

        history.removeAll(tasksToRemove);
    }
}
