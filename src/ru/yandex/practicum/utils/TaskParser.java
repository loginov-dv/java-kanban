package ru.yandex.practicum.utils;

import ru.yandex.practicum.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static ru.yandex.practicum.utils.CsvUtils.parseLine;

// Вспомогательный класс для парсинга задач из файла
public final class TaskParser {
    // Константы индексов полей задач в файле
    public static final int ID_INDEX = 0;
    public static final int TYPE_INDEX = 1;
    public static final int NAME_INDEX = 2;
    public static final int STATUS_INDEX = 3;
    public static final int DESCRIPTION_INDEX = 4;
    public static final int START_TIME_INDEX = 5;
    public static final int DURATION_INDEX = 6;
    public static final int EPIC_ID_INDEX = 7;

    // Количество полей (в т.ч. пустых)
    public static final int FIELDS_COUNT = 8;

    // Заголовок файла при чтении/сохранении
    public static final String HEADER = "id,type,name,status,description,startTime,duration,epic";

    // Метод для парсинга задач
    public static Task parse(String line) throws IllegalArgumentException {
        List<String> parts = parseLine(line);

        if (parts.size() != FIELDS_COUNT) {
            throw new IllegalArgumentException("Некорректный формат строки");
        }

        TaskType type = TaskType.valueOf(parts.get(TYPE_INDEX).toUpperCase());
        int id = Integer.parseInt(parts.get(ID_INDEX));
        String name = parts.get(NAME_INDEX);
        TaskStatus status = TaskStatus.valueOf(parts.get(STATUS_INDEX));
        String description = parts.get(DESCRIPTION_INDEX);
        LocalDateTime startTime = parts.get(START_TIME_INDEX).isEmpty()
                ? null
                : LocalDateTime.parse(parts.get(START_TIME_INDEX));
        Duration duration = Duration.ofMinutes(Long.parseLong(parts.get(DURATION_INDEX)));

        return switch (type) {
            case TASK -> new Task(id, name, description, status, startTime, duration);
            case SUBTASK -> {
                int epicID = Integer.parseInt(parts.get(EPIC_ID_INDEX));
                yield new Subtask(id, name, description, status, epicID, startTime, duration);
            }
            case EPIC -> new Epic(id, name, description, status, startTime, duration);
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        };
    }
}
