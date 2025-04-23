package ru.yandex.practicum.tasks;

// Класс для описания подзадачи в рамках эпика
public class Subtask extends Task {
    // Идентификатор эпика, в рамках которого выполняется задача
    private Integer epicID;

    // Конструктор класса Subtask
    public Subtask(int id, String name, String description, TaskStatus status, Integer epicID) {
        super(id, name, description, status);
        this.epicID = epicID;
    }

    // Получить идентификатор эпика
    public Integer getEpicID() {
        return epicID;
    }

    // Задать идентификатор эпика
    public void setEpicID(Integer id) {
        // Проверка на добавление самой себя в эпик
        if (id != null && this.getId() == id) {
            return;
        }

        epicID = id;
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
