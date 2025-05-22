package ru.yandex.practicum.tasks;

import java.util.ArrayList;
import java.util.List;

// Класс для описания эпика (большой задачи)
public class Epic extends Task {
    // id подзадач, входящих в эпик
    private final List<Integer> subtaskIDs;

    // Конструктор класса Epic
    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        subtaskIDs = new ArrayList<>();
    }

    // Конструктор класса Epic
    public Epic(int id, String name, String description, TaskStatus status, List<Integer> subtaskIDs) {
        this(id, name, description, status);
        // Не добавляем, если id подзадачи равен null или равен id самого эпика
        for (Integer subtaskID : subtaskIDs) {
            if (subtaskID != null && !subtaskID.equals(id)) {
                this.subtaskIDs.add(subtaskID);
            }
        }
    }

    // Конструктор копирования
    protected Epic(Epic otherEpic) {
        super(otherEpic);
        this.subtaskIDs = new ArrayList<>(otherEpic.subtaskIDs);
    }

    // Получить список id всех подзадач
    public List<Integer> getSubtaskIDs() {
        return subtaskIDs;
    }

    // Возвращает копию текущего объекта Epic
    @Override
    public Epic copy() {
        return new Epic(this);
    }

    @Override
    public String toString() {
        String resultSuper = super.toString();
        String contentSuper = resultSuper.substring(resultSuper.indexOf("{"), resultSuper.indexOf("}"));
        String result = "Epic" + contentSuper;
        result += subtaskIDs.isEmpty() ? ", subtasks=empty}" : (", subtasks.size=" + subtaskIDs.size() + "}");

        return result;
    }
}
