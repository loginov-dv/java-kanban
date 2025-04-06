public class Subtask extends Task {
    Epic epic;

    Subtask(int id, String name, String description, Epic epic) {
        super(id, name, description);
        this.epic = epic;
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
