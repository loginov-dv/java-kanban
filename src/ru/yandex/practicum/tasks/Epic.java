package ru.yandex.practicum.tasks;

import java.util.ArrayList;
import java.util.List;

// Класс для описания эпика (большой задачи)
public class Epic extends Task {
    // Идентификаторы подзадач, входящих в эпик
    private List<Integer> subtaskIDs;

    // Конструктор класса Epic
    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        subtaskIDs = new ArrayList<>();
    }
    // Конструктор класса Epic
    public Epic(int id, String name, String description, TaskStatus status, List<Integer> subtaskIDs) {
        this(id, name, description, status);
        this.subtaskIDs = subtaskIDs;
    }

    // Получить список идентификаторов всех подзадач
    public List<Integer> getSubtaskIDs() {
        return subtaskIDs;
    }

    // Добавить идентификатор подзадачи в эпик
    public void addSubtask(Integer id) {
        // Проверка на добавление null
        if (id == null) {
            return;
        }
        // Проверка на добавление самого себя в подзадачи
        if (this.getId() == id) {
            return;
        }
        // Ничего не делаем, если уже есть подзадача с таким идентификатором
        if (subtaskIDs.contains(id)) {
            return;
        }

        subtaskIDs.add(id);
    }

    // Удалить идентификатор подзадачи
    public void removeSubtask(Integer id) {
        subtaskIDs.remove(id);
    }

    // Удалить идентификаторы всех подзадач
    public void removeAllSubtasks() {
        subtaskIDs.clear();
    }

    @Override
    public String toString() {
        String resultSuper = super.toString();
        String contentSuper = resultSuper.substring(resultSuper.indexOf("{"), resultSuper.indexOf("}"));
        String result = "Epic" + contentSuper;
        result += subtaskIDs.isEmpty() ? ", subtasks=empty}" : (", subtasks.size=" + subtaskIDs.size() + "}") ;

        return result;
    }
}
