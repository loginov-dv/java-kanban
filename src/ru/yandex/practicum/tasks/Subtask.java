package ru.yandex.practicum.tasks;

import java.util.Objects;

// Класс для описания подзадачи в рамках эпика
public class Subtask extends Task {
    // id эпика, в рамках которого выполняется задача
    private Integer epicID;

    // Конструктор класса Subtask
    public Subtask(int id, String name, String description, TaskStatus status, Integer epicID) {
        super(id, name, description, status);
        // Не добавляем, если id эпика равен id самой подзадачи
        if (!Objects.equals(epicID, id)) {
            this.epicID = epicID;
        }
    }

    // Конструктор копирования
    protected Subtask(Subtask otherSubtask) {
        super(otherSubtask);
        this.epicID = otherSubtask.epicID;
    }

    // Получить id эпика
    public Integer getEpicID() {
        return epicID;
    }

    // Возвращает копию текущего объекта Subtask
    @Override
    public Subtask copy() {
        return new Subtask(this);
    }

    @Override
    public String toString() {
        return super.toString() + getEpicID();
    }
}
