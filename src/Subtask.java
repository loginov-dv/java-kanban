public class Subtask extends Task {
    Epic epic;

    Subtask(int id, String name, String description, Epic epic) {
        super(id, name, description);
        this.epic = epic;
    }
}
