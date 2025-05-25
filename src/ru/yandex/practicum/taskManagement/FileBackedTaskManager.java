package ru.yandex.practicum.taskManagement;

public class FileBackedTaskManager extends InMemoryTaskManager {

    // Путь к файлу автосохранения
    private final String fileName;

    // Конструктор класса FileBackedTaskManager
    public FileBackedTaskManager(String fileName) {
        super();
        this.fileName = fileName;
    }

    // Конструктор класса FileBackedTaskManager
    public FileBackedTaskManager(HistoryManager historyManager, String fileName) {
        super(historyManager);
        this.fileName = fileName;
    }
}
