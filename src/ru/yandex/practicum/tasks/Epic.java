package ru.yandex.practicum.tasks;

import java.util.ArrayList;
import java.util.List;

import static ru.yandex.practicum.utils.CSVUtils.parseLine;

// Класс для описания эпика (большой задачи)
public class Epic extends Task {
    // id подзадач, входящих в эпик
    private final List<Integer> subtaskIDs;

    // Конструктор класса Epic
    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        subtaskIDs = new ArrayList<>();
    }

    // Конструктор класса Epic
    public Epic(int id, String name, String description, TaskStatus status, List<Integer> subtaskIDs) {
        this(id, name, description, status);
        // Не добавляем, если id подзадачи равен null или равен id самого эпика
        for (Integer subtaskID : subtaskIDs) {
            if (subtaskID != null && !subtaskID.equals(id)) {
                this.subtaskIDs.add(subtaskID);
            }
        }
    }

    // Конструктор копирования
    protected Epic(Epic otherEpic) {
        super(otherEpic);
        this.subtaskIDs = new ArrayList<>(otherEpic.subtaskIDs);
    }

    // Получить список id всех подзадач
    public List<Integer> getSubtaskIDs() {
        return subtaskIDs;
    }

    // Создать объект класса Epic из его строкового представления
    public static Epic fromString(String value) {
        List<String> args = parseLine(value);

        if (args.size() < 5 || args.size() > 6) {
            throw new IllegalArgumentException("Некорректный формат строки");
        }

        int id = Integer.parseInt(args.get(0));
        String name = args.get(2);
        TaskStatus status = TaskStatus.valueOf(args.get(3));
        String description = args.get(4);

        return new Epic(id, name, description, status);
    }

    // Возвращает копию текущего объекта Epic
    @Override
    public Epic copy() {
        return new Epic(this);
    }
}
