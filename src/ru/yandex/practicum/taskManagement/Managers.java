package ru.yandex.practicum.taskManagement;

// Утилитарный класс для создания менеджеров задач
public class Managers {

    // Создать объект класса, реализующего TaskManager, по умолчанию
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    // Создать объект класса, реализующего HistoryManager, по умолчанию
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
