package ru.yandex.practicum.Tasks;

import java.util.ArrayList;

// Класс для описания эпика (большой задачи)
public class Epic extends Task {
    // Идентификаторы подзадач, входящих в эпик
    private ArrayList<Integer> subtaskIDs;

    // Конструктор класса Epic
    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        subtaskIDs = new ArrayList<>();
    }
    // Конструктор класса Epic
    public Epic(int id, String name, String description, TaskStatus status, ArrayList<Integer> subtaskIDs) {
        this(id, name, description, status);
        this.subtaskIDs = subtaskIDs;
    }

    // Получить список идентификаторов всех подзадач
    public ArrayList<Integer> getSubtaskIDs() {
        return subtaskIDs;
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
