package ru.yandex.practicum.Tasks;

import java.util.Objects;

// Базовый класс для описания задачи
public class Task {
    private final String name;
    private final String description;
    private final int id;
    private final TaskStatus status;

    public Task(int id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    // Получить имя задачи
    public String getName() {
        return name;
    }

    // Получить описание задачи
    public String getDescription() {
        return description;
    }

    // Получить идентификатор задачи
    public int getId() {
        return id;
    }

    // Получить текущий статус задачи
    public TaskStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Task task = (Task)obj;
        return id == task.id;
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
