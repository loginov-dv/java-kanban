package ru.yandex.practicum.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.utils.CsvUtils.escapeSpecialCharacters;

// Класс для описания эпика (большой задачи)
public class Epic extends Task {
    // id подзадач, входящих в эпик
    private final List<Integer> subtaskIDs;

    // Дата и время завершения задачи
    private LocalDateTime endTime;

    // Конструктор класса Epic (для вновь создаваемых эпиков)
    public Epic(int id, String name, String description) {
        this(id, name, description, TaskStatus.NEW);
    }

    // Конструктор класса Epic (для вновь создаваемых эпиков)
    public Epic(int id, String name, String description, TaskStatus status) {
        // startTime и duration - расчётные, здесь присваиваем некоторые значения по умолчанию
        this(id, name, description, status, null, Duration.ZERO);
    }

    // Конструктор класса Epic
    public Epic(int id, String name, String description, TaskStatus status, LocalDateTime startTime,
                Duration duration) {
        super(id, name, description, status, startTime, duration);
        subtaskIDs = new ArrayList<>();
        type = TaskType.EPIC;
    }

    // Конструктор класса Epic
    public Epic(int id, String name, String description, TaskStatus status, List<Integer> subtaskIDs,
                LocalDateTime startTime, Duration duration) {
        this(id, name, description, status, startTime, duration);
        // Не добавляем, если id подзадачи равен null или равен id самого эпика
        subtaskIDs.stream()
                .filter(subtaskID -> subtaskID != null && !subtaskID.equals(id))
                .forEach(this.subtaskIDs::add);
    }

    // Конструктор класса Epic
    public Epic(int id, String name, String description, TaskStatus status, List<Integer> subtaskIDs,
                LocalDateTime startTime, Duration duration, LocalDateTime endTime) {
        this(id, name, description, status, subtaskIDs, startTime, duration);
        this.endTime = endTime;
    }

    // Конструктор копирования класса Epic
    protected Epic(Epic otherEpic) {
        super(otherEpic);
        this.subtaskIDs = new ArrayList<>(otherEpic.subtaskIDs);
        this.endTime = otherEpic.endTime;
        this.type = TaskType.EPIC;
    }

    // Получить список id всех подзадач
    public List<Integer> getSubtaskIDs() {
        return subtaskIDs;
    }

    // Возвращает копию текущего объекта Epic
    @Override
    public Epic copy() {
        return new Epic(this);
    }

    @Override
    public String toString() {
        return getID() + "," + TaskType.EPIC.getDisplayName() + "," + escapeSpecialCharacters(getName()) + ","
                + getStatus().name() + "," + escapeSpecialCharacters(getDescription()) + ","
                + (getStartTime().isPresent() ? getStartTime().get().toString() : "") + ","
                + (getDuration() != null ? getDuration().toMinutes() : "") + ",";
    }

    @Override
    public Optional<LocalDateTime> getEndTime() {
        return Optional.ofNullable(endTime);
    }
}
