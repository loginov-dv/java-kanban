package ru.yandex.practicum.taskManagement;

// Утилитарный класс для создания менеджеров задач
public class Manager {

    // Создать объект класса, реализующего TaskManager, на основе информации о типе
    public static TaskManager getTaskManager(int type) {
        if (type == 1) {
            return new InMemoryTaskManager();
        } else {
            return getDefault();
        }
    }

    // Создать объект класса по умолчанию, реализующего TaskManager
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
}
