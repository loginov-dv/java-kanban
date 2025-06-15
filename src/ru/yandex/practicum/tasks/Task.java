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
    private Duration duration;
    // Дата и время начала выполнения задачи
    private LocalDateTime startTime;

    // Конструктор класса Task
    public Task(int id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    // Конструктор класса Task
    public Task(int id, String name, String description, TaskStatus status, LocalDateTime startTime,
                Duration duration) {
        this(id, name, description, status);
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

    // Создать объект Task из его строкового представления
    public static Task fromString(String value) throws IllegalArgumentException {
        List<String> args = parseLine(value);
        // TODO: вынести логику и магические числа в отдельный класс
        if (args.size() != 8) {
            throw new IllegalArgumentException("Некорректный формат строки");
        }

        int id = Integer.parseInt(args.get(0));
        String name = args.get(2);
        TaskStatus status = TaskStatus.valueOf(args.get(3));
        String description = args.get(4);
        LocalDateTime startTime = LocalDateTime.parse(args.get(5));
        Duration duration = Duration.ofMinutes(Long.parseLong(args.get(6)));

        return new Task(id, name, description, status, startTime, duration);
    }

    // Получить дату и время завершения задачи
    public LocalDateTime getEndTime() {
        // TODO: а если нету даты и времени начала?
        return startTime.plus(duration);
    }

    // Проверяет пересечение двух задач по времени выполнения
    public final boolean hasIntersectionWith(Task otherTask) {
        // Если у одной из задач не указана дата и время начала, то не можем определить пересечение
        if (getStartTime() == null || otherTask.getStartTime() == null) {
            return false;
        }

        // Определение пересечения по методу наложения отрезков
        return otherTask.getStartTime().isBefore(getEndTime())
                || getEndTime().isAfter(otherTask.getStartTime());
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
