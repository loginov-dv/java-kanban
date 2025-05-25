package ru.yandex.practicum.tasks;

import java.util.Objects;

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
        String[] args = value.split(",");

        if (args.length < 5) {
            throw new IllegalArgumentException("Некорректный формат строки");
        }

        int id = Integer.parseInt(args[0]);
        String name = args[2];
        TaskStatus status = TaskStatus.valueOf(args[3]);
        String description = args[4];

        return new Task(id, name, description, status);
    }

    // Идентификация задачи происходит по id, т.е. две задачи с одним и тем же id считаются одинаковыми
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Task task = (Task)obj;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getID() + "," + this.getClass().getName() + "," + getName() + "," + getStatus().name() +
                "," + getDescription() + ",";
    }
}
