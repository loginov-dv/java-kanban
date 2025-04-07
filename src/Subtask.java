// Класс для описания подзадачи в рамках эпика
public class Subtask extends Task {
    private Epic epic;

    // Полагаем, что подзадача не существует сама по себе, поэтому в конструктор нужно передать эпик
    public Subtask(int id, String name, String description, Epic epic) {
        super(id, name, description);
        this.epic = epic;
        // Добавляем подзадачу в эпик
        epic.addSubtask(this);
    }

    //
    public void removeEpic() {
        epic.removeSubtask(this);
        epic = null;
    }

    @Override
    public void setStatus(TaskStatus status) {
        // Изменяем статус подзадачи
        super.setStatus(status);
        // Обновляем статус эпика
        epic.updateStatus();
    }

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
