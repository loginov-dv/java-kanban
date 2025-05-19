package ru.yandex.practicum.tasks;

// Класс для описания подзадачи в рамках эпика
public class Subtask extends Task {
    // id эпика, в рамках которого выполняется задача
    private Integer epicID;

    // Конструктор класса Subtask
    public Subtask(int id, String name, String description, TaskStatus status, Integer epicID) {
        super(id, name, description, status);
        // Не добавляем, если id эпика равен id самой подзадачи
        if (epicID != Integer.valueOf(id)) {
            this.epicID = epicID;
        }
    }

    // Конструктор копирования
    protected Subtask(Subtask otherSubtask) {
        super(otherSubtask);
        this.epicID = otherSubtask.epicID;
    }

    // Получить id эпика
    public Integer getEpicID() {
        return epicID;
    }

    // Возвращает копию текущего объекта Subtask
    @Override
    public Subtask copy() {
        return new Subtask(this);
    }

    @Override
    public String toString() {
        String resultSuper = super.toString();
        String fieldsOfSuper = resultSuper.substring(resultSuper.indexOf("{"), resultSuper.indexOf("}"));
        String result = "Subtask" + fieldsOfSuper;
        result += epicID == null ? ", epic=null}" : (", epic.id=" + epicID + "}");

        return result;
    }
}
