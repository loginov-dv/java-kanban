package ru.yandex.practicum.Tasks;

import java.util.ArrayList;

// Класс для описания эпика (большой задачи)
public class Epic extends Task {
    // Подзадачи, входящие в эпик
    private ArrayList<Subtask> subtasks;

    // Конструктор класса Epic
    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        subtasks = new ArrayList<>();
    }
    // Конструктор класса Epic
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
        // Если уже есть подзадача с таким идентификатором, то ничего не делаем
        if (subtasks.contains(subtask)) {
            return;
        }

        subtasks.add(subtask);
    }

    // Удалить подзадачу
    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    // Обновить подзадачу
    public void updateSubtask(Subtask updatedSubtask) {
        // Обновляем, если есть подзадача с таким же id
        int subtaskIndex = subtasks.indexOf(updatedSubtask);

        if (subtaskIndex >= 0) {
            subtasks.set(subtaskIndex, updatedSubtask);
        }
    }

    @Override
    public String toString() {
        String resultSuper = super.toString();
        String contentSuper = resultSuper.substring(resultSuper.indexOf("{"), resultSuper.indexOf("}"));
        String result = "Epic" + contentSuper;
        result += subtasks.isEmpty() ? ", subtasks=empty}" : (", subtasks.size=" + subtasks.size() + "}") ;

        return result;
    }
}
