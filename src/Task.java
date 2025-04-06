import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private final int id;
    private TaskStatus status;

    Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        status = TaskStatus.NEW;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Task task = (Task)obj;
        return id == task.id /*|| (name.equals(task.name) && description.equals(task.description))*/;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String result = "Task{id=" + id +", ";
        if (name != null) {
            result += "name=" + name + ", ";
        } else {
            result += "name=null, ";
        }
        if (description != null) {
            result += "description.length=" + description.length() + ", ";
        } else {
            result += "description=null, ";
        }
        result += "status=" + status.name() + "}";

        return result;
    }
}
