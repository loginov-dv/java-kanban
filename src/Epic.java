import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;



    public Epic(int id, String name, String description, ArrayList<Subtask> subtasks) {
        this(id, name,description);
        this.subtasks = subtasks;
    }

    public Epic(int id, String name, String description) {
        super(id, name, description);
        subtasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        //subtask.setEpic(this);
        updateStatus();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        subtask.removeEpic();
        updateStatus();
    }

    public void updateStatus() {
        if (subtasks.isEmpty()) {
            setStatus(TaskStatus.NEW);
        } else {
            int newSubtasks = 0;
            int doneSubtasks = 0;

            for (Task subtask : subtasks) {
                switch (subtask.getStatus()) {
                    case NEW:
                        newSubtasks++;
                        break;
                    case DONE:
                        doneSubtasks++;
                        break;
                }
            }

            if (newSubtasks == subtasks.size()) {
                setStatus(TaskStatus.NEW);
            } else if (doneSubtasks == subtasks.size()) {
                setStatus(TaskStatus.DONE);
            } else {
                setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    // TODO: setStatus?

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
