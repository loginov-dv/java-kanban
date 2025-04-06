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
}
