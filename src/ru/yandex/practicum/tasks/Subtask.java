package ru.yandex.practicum.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static ru.yandex.practicum.utils.CsvUtils.escapeSpecialCharacters;

// Класс для описания подзадачи в рамках эпика
public class Subtask extends Task {
    // id эпика, в рамках которого выполняется задача
    private Integer epicID;

    // Конструктор класса Subtask
    public Subtask(int id, String name, String description, TaskStatus status, Integer epicID,
                   LocalDateTime startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
        // Не добавляем, если id эпика равен id самой подзадачи
        if (!Objects.equals(epicID, id)) {
            this.epicID = epicID;
        }
    }

    // Конструктор копирования класса Subtask
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
        return getID() + "," + TaskType.SUBTASK.getDisplayName() + "," + escapeSpecialCharacters(getName()) + ","
                + getStatus().name() + "," + escapeSpecialCharacters(getDescription()) + ","
                + (getStartTime().isPresent() ? getStartTime().get().toString() : "") + ","
                + (getDuration() != null ? getDuration().toMinutes() : "") + "," + getEpicID();
    }
}
