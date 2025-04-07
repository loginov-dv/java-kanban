public class Subtask extends Task {
    private Epic epic;

    public Subtask(int id, String name, String description, Epic epic) {
        super(id, name, description);
        this.epic = epic;
        epic.addSubtask(this);
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public void removeEpic() {
        this.epic = null;
    }

    @Override
    public void setStatus(TaskStatus status) {
        super.setStatus(status);
        epic.updateStatus();
    }

    @Override
    public String toString() {
        String resultSuper = super.toString();
        String contentSuper = resultSuper.substring(resultSuper.indexOf("{"), resultSuper.indexOf("}"));
        String result = "Subtask" + contentSuper;

        if (epic != null) {
            result += ", epic.id=" + epic.getId() + "}";
        } else {
            result += ", epic=null}";
        }

        return result;
    }
}
