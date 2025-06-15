package ru.yandex.practicum.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static ru.yandex.practicum.utils.CSVUtils.escapeSpecialCharacters;
import static ru.yandex.practicum.utils.CSVUtils.parseLine;

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

    // Создать объект класса Subtask из его строкового представления
    public static Subtask fromString(String value) throws IllegalArgumentException {
        List<String> args = parseLine(value);

        if (args.size() != 8) {
            throw new IllegalArgumentException("Некорректный формат строки");
        }

        int id = Integer.parseInt(args.get(0));
        String name = args.get(2);
        TaskStatus status = TaskStatus.valueOf(args.get(3));
        String description = args.get(4);
        LocalDateTime startTime;
        if (args.get(5).isEmpty()) {
            startTime = null;
        } else {
            startTime = LocalDateTime.parse(args.get(5));
        }
        Duration duration = Duration.ofMinutes(Long.parseLong(args.get(6)));
        int epicID = Integer.parseInt(args.get(7));

        return new Subtask(id, name, description, status, epicID, startTime, duration);
    }

    @Override
    public String toString() {
        return getID() + "," + TaskType.SUBTASK.getDisplayName() + "," + escapeSpecialCharacters(getName()) + ","
                + getStatus().name() + "," + escapeSpecialCharacters(getDescription()) + ","
                + (getStartTime() != null ? getStartTime().toString() : "") + ","
                + (getDuration() != null ? getDuration().toMinutes() : "") + "," + getEpicID();
    }
}
