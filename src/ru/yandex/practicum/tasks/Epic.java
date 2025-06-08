package ru.yandex.practicum.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.yandex.practicum.utils.CSVUtils.escapeSpecialCharacters;
import static ru.yandex.practicum.utils.CSVUtils.parseLine;

// Класс для описания эпика (большой задачи)
public class Epic extends Task {
    // id подзадач, входящих в эпик
    private final List<Integer> subtaskIDs;

    // Дата и время завершения задачи
    private LocalDateTime endTime;

    // Конструктор класса Epic
    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        subtaskIDs = new ArrayList<>();
    }

    // Конструктор класса Epic
    public Epic(int id, String name, String description, TaskStatus status, List<Integer> subtaskIDs) {
        this(id, name, description, status);
        // Не добавляем, если id подзадачи равен null или равен id самого эпика
        subtaskIDs.stream()
                .filter(subtaskID -> subtaskID != null && !subtaskID.equals(id))
                .forEach(this.subtaskIDs::add);
    }

    // Конструктор класса Epic
    public Epic(int id, String name, String description, TaskStatus status, LocalDateTime startTime,
                Duration duration) {
        super(id, name, description, status, startTime, duration);
        subtaskIDs = new ArrayList<>();
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

    // Конструктор копирования класса Epic
    protected Epic(Epic otherEpic) {
        super(otherEpic);
        this.subtaskIDs = new ArrayList<>(otherEpic.subtaskIDs);
    }

    // Получить список id всех подзадач
    public List<Integer> getSubtaskIDs() {
        return subtaskIDs;
    }

    // Создать объект класса Epic из его строкового представления
    public static Epic fromString(String value) throws IllegalArgumentException {
        List<String> args = parseLine(value);

        if (args.size() != 7) {
            throw new IllegalArgumentException("Некорректный формат строки");
        }

        int id = Integer.parseInt(args.get(0));
        String name = args.get(2);
        TaskStatus status = TaskStatus.valueOf(args.get(3));
        String description = args.get(4);
        LocalDateTime startTime = LocalDateTime.parse(args.get(5));
        Duration duration = Duration.ofMinutes(Long.parseLong(args.get(6)));

        return new Epic(id, name, description, status, startTime, duration);
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
                + getStartTime().toString() + "," + getDuration().toMinutes() + ",";
    }
}
