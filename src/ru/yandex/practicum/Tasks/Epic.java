package ru.yandex.practicum.Tasks;

import java.util.ArrayList;
import java.util.HashMap;

// Класс для описания эпика (большой задачи)
public class Epic extends Task {
    private ArrayList<Subtask> subtasks;

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        subtasks = new ArrayList<>();
    }

    public Epic(int id, String name, String description, TaskStatus status, ArrayList<Subtask> subtasks) {
        this(id, name, description, status);
        this.subtasks = subtasks;
    }

    // Получить список всех подзадач
    public ArrayList<Subtask> getAllSubtasks() {
        return subtasks;
    }

    // Добавить новую подзадачу
    public void addSubtask(Subtask subtask) {
        // Ничего не делаем, если уже есть подзадача с таким идентификатором
        if (subtasks.contains(subtask)) {
            return;
        }

        subtasks.add(subtask);
    }

    // Удалить подзадачу
    public void removeSubtask(Subtask subtask) {
        // Сравнение по equals даст true, т.к. id подзадач одинаковы
        subtasks.remove(subtask);
    }

    // Обновить подзадачу
    public void updateSubtask(Subtask updatedSubtask) {
        // Сравнение по equals даст true, т.к. id подзадач одинаковы
        int subtaskIndex = subtasks.indexOf(updatedSubtask);

        // Если такой подзадачи нет, то нечего обновлять
        if (subtaskIndex >= 0) {
            subtasks.set(subtaskIndex, updatedSubtask);
        }
    }

    @Override
    public String toString() {
        String resultSuper = super.toString();
        String contentSuper = resultSuper.substring(resultSuper.indexOf("{"), resultSuper.indexOf("}"));
        String result = "Epic" + contentSuper;

        if (!subtasks.isEmpty()) {
            result += ", subtasks.size=" + subtasks.size() + "}";
        } else {
            result += ", subtasks=empty}";
        }

        return result;
    }
}
