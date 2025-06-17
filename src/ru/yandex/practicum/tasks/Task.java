package ru.yandex.practicum.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static ru.yandex.practicum.utils.CSVUtils.escapeSpecialCharacters;
import static ru.yandex.practicum.utils.CSVUtils.parseLine;

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
        this.startTime = otherTask.getStartTime();
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
    public LocalDateTime getStartTime() {
        return startTime;
    }

    // Возвращает копию текущего объекта Task
    public Task copy() {
        return new Task(this);
    }

    // Получить дату и время завершения задачи
    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    // Проверяет пересечение двух задач по времени выполнения
    public final boolean hasOverlapWith(Task otherTask) {
        // Если у одной из задач не указана дата и время начала, то не можем определить пересечение
        if (getStartTime() == null || otherTask.getStartTime() == null) {
            return false;
        }

        // Примыкающие друг к другу задачи, не считаем пересечениями
        if (otherTask.getStartTime().isEqual(getEndTime()) || otherTask.getEndTime().isEqual(getStartTime())) {
            return false;
        }

        // Определение пересечения по методу наложения отрезков
        return (otherTask.getStartTime().isAfter(getStartTime()) && otherTask.getStartTime().isBefore(getEndTime()))
                || (otherTask.getEndTime().isAfter(getStartTime()) && otherTask.getEndTime().isBefore(getEndTime()))
                || (otherTask.getStartTime().isAfter(getStartTime()) && otherTask.getEndTime().isBefore(getEndTime()))
                || (otherTask.getStartTime().isBefore(getStartTime()) && otherTask.getEndTime().isAfter(getEndTime()));
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
                + (getStartTime() != null ? getStartTime().toString() : "") + ","
                + (getDuration() != null ? getDuration().toMinutes() : "") + ",";
    }
}
