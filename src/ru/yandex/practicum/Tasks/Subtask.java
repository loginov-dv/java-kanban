package ru.yandex.practicum.Tasks;

// Класс для описания подзадачи в рамках эпика
public class Subtask extends Task {
    private Epic epic;

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

    /*public void removeEpic() {
        epic.removeSubtask(this);
        epic = null;
    }*/

    @Override
    public String toString() {
        String resultSuper = super.toString();
        String fieldsOfSuper = resultSuper.substring(resultSuper.indexOf("{"), resultSuper.indexOf("}"));
        String result = "Subtask" + fieldsOfSuper;

        if (epic != null) {
            result += ", epic.id=" + epic.getId() + "}";
        } else {
            result += ", epic=null}";
        }

        return result;
    }
}
