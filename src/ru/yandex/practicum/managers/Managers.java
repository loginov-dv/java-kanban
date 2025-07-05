package ru.yandex.practicum.managers;

import java.io.File;
import java.io.IOException;

// Утилитарный класс для создания менеджеров задач
public final class Managers {

    // Создать объект класса, реализующего TaskManager, по умолчанию
    public static TaskManager getDefault() {
        return getInMemoryTaskManager();
    }

    // Создать объект класса InMemoryTaskManager
    public static InMemoryTaskManager getInMemoryTaskManager() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    // Создать объект класса FileBackedTaskManager
    public static FileBackedTaskManager getFileBackedTaskManager() throws IOException {
        File saveFile = File.createTempFile("autoSave", ".csv");
        saveFile.deleteOnExit();

        return new FileBackedTaskManager(saveFile);
    }

    // Создать объект класса, реализующего HistoryManager, по умолчанию
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
