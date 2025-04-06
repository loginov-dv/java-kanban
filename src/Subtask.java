public class Subtask extends Task {
    Epic epic;

    Subtask(int id, String name, String description, Epic epic) {
        super(id, name, description);
        this.epic = epic;
    }

    @Override
    public String toString() {
        String result = super.toString();
        // подстрока без конечной фигурной скобки
        result = result.substring(0, result.length() - 2);
        if (epic != null) {
            result += ", epic.id=" + epic.getId() + "}";
        } else {
            result += ", epic=null}";
        }

        return result;
    }
}
