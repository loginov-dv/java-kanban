package ru.yandex.practicum.tasks;

// Тип задачи
public enum TaskType {
    TASK("Task"),
    EPIC("Epic"),
    SUBTASK("Subtask");

    // Наименование типа задачи
    private final String displayName;

    // Конструктор TaskType
    TaskType(String displayName) {
        this.displayName = displayName;
    }

    // Получить наименование типа задачи
    public String getDisplayName() {
        return displayName;
    }
}
