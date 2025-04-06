import java.util.ArrayList;

public class Epic extends Task {
    ArrayList<Subtask> subtasks;

    Epic(int id, String name, String description, ArrayList<Subtask> subtasks) {
        this(id, name,description);
        this.subtasks = subtasks;
    }

    Epic(int id, String name, String description) {
        super(id, name, description);
    }
}
