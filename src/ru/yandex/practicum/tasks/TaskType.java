package ru.yandex.practicum.tasks;

// Тип задачи
public enum TaskType {
    TASK("Task"),
    EPIC("Epic"),
    SUBTASK("Subtask");

    // Наименование типа задачи для вывода
    private final String displayName;

    // Конструктор TaskType
    TaskType(String displayName) {
        this.displayName = displayName;
    }

    // Получить тип задачи
    public String getDisplayName() {
        return displayName;
    }
}
