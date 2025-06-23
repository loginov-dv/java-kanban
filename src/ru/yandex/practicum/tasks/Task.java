package ru.yandex.practicum.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static ru.yandex.practicum.utils.CSVUtils.escapeSpecialCharacters;

// Базовый класс для описания задачи
public class Task {
    // Название
    private final String name;
    // Описание
    private final String description;
    // Уникальный идентификационный номер задачи
    private final int id;
    // Статус
    private final TaskStatus status;
    // Продолжительность выполнения задачи
    private final Duration duration;
    // Дата и время начала выполнения задачи
    private final LocalDateTime startTime;

    // Конструктор класса Task
    public Task(int id, String name, String description, TaskStatus status, LocalDateTime startTime,
                Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    // Конструктор копирования класса Task
    protected Task(Task otherTask) {
        this.name = otherTask.getName();
        this.description = otherTask.getDescription();
        this.id = otherTask.getID();
        this.status = otherTask.getStatus();
        this.startTime = otherTask.getStartTime().orElse(null);
        this.duration = otherTask.getDuration();
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
    public int getID() {
        return id;
    }

    // Получить текущий статус задачи
    public TaskStatus getStatus() {
        return status;
    }

    // Получить продолжительность задачи
    public Duration getDuration() {
        return duration;
    }

    // Получить дату и время начала задачи
    public Optional<LocalDateTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    // Возвращает копию текущего объекта Task
    public Task copy() {
        return new Task(this);
    }

    // Получить дату и время завершения задачи
    public Optional<LocalDateTime> getEndTime() {
        return (startTime == null || duration == null)
                ? Optional.empty()
                : Optional.of(startTime.plus(duration));
    }

    // Проверяет пересечение двух задач по времени выполнения
    public final boolean hasOverlapWith(Task otherTask) {
        // Если у одной из задач не указана дата и время окончания,
        // значит у неё отсутствует либо дата и время начала, либо продолжительность.
        // В таком случае не можем определить пересечение
        if (getEndTime().isEmpty() || otherTask.getEndTime().isEmpty()) {
            return false;
        }

        // Определение пересечения по методу наложения отрезков
        // Проверка isPresent() избыточна, поэтому опущена
        return this.getStartTime().get().isBefore(otherTask.getEndTime().get())
                && this.getEndTime().get().isAfter(otherTask.getStartTime().get());
    }

    // Идентификация задачи происходит по id, т.е. две задачи с одним и тем же id считаются одинаковыми
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getID() + "," + TaskType.TASK.getDisplayName() + "," + escapeSpecialCharacters(getName()) + ","
                + getStatus().name() + "," + escapeSpecialCharacters(getDescription()) + ","
                + (getStartTime().isPresent() ? getStartTime().get().toString() : "") + ","
                + (getDuration() != null ? getDuration().toMinutes() : "") + ",";
    }
}
