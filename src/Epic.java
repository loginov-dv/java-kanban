import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;

    Epic(int id, String name, String description, ArrayList<Subtask> subtasks) {
        this(id, name,description);
        this.subtasks = subtasks;
    }

    Epic(int id, String name, String description) {
        super(id, name, description);
        subtasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        String result = super.toString();
        // подстрока без конечной фигурной скобки
        result = result.substring(0, result.length() - 2);
        if (!subtasks.isEmpty()) {
            result += ", subtasks.size=" + subtasks.size() + "}";
        } else {
            result += ", subtasks=empty}";
        }

        return result;
    }
}
