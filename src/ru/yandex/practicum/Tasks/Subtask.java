package ru.yandex.practicum.Tasks;

// Класс для описания подзадачи в рамках эпика
public class Subtask extends Task {
    // Эпик, в рамках которого выполняется задача
    private Epic epic;

    // Конструктор класса Subtask
    public Subtask(int id, String name, String description, TaskStatus status, Epic epic) {
        super(id, name, description, status);
        this.epic = epic;
    }

    // Получить эпик
    public Epic getEpic() {
        return epic;
    }

    // Задать эпик
    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public String toString() {
        String resultSuper = super.toString();
        String fieldsOfSuper = resultSuper.substring(resultSuper.indexOf("{"), resultSuper.indexOf("}"));
        String result = "Subtask" + fieldsOfSuper;
        result += epic == null ? ", epic=null}" : (", epic.id=" + epic.getId() + "}");

        return result;
    }
}
