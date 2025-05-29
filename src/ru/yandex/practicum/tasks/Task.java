package ru.yandex.practicum.tasks;

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

    // Конструктор класса Task
    public Task(int id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    // Конструктор копирования
    protected Task(Task otherTask) {
        this.name = otherTask.getName();
        this.description = otherTask.getDescription();
        this.id = otherTask.getID();
        this.status = otherTask.getStatus();
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

    // Возвращает копию текущего объекта Task
    public Task copy() {
        return new Task(this);
    }

    // Создать объект Task из его строкового представления
    public static Task fromString(String value) {
        List<String> args = parseLine(value);

        if (args.size() < 5 || args.size() > 6) {
            throw new IllegalArgumentException("Некорректный формат строки");
        }

        int id = Integer.parseInt(args.get(0));
        String name = args.get(2);
        TaskStatus status = TaskStatus.valueOf(args.get(3));
        String description = args.get(4);

        return new Task(id, name, description, status);
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
        return getID() + "," + this.getClass().getSimpleName() + "," + escapeSpecialCharacters(getName()) + ","
                + getStatus().name() + "," + escapeSpecialCharacters(getDescription()) + ",";
    }


}
