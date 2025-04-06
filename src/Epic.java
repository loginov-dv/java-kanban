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

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }


    @Override
    public String toString() {
        String resultSuper = super.toString();
        String contentSuper = resultSuper.substring(resultSuper.indexOf("{"), resultSuper.indexOf("}"));
        String result = "Epic" + contentSuper;

        if (!subtasks.isEmpty()) {
            result += ", subtasks.size=" + subtasks.size() + "}";
        } else {
            result += ", subtasks=empty}";
        }

        return result;
    }
}
