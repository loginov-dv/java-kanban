package ru.yandex.practicum.exceptions;

// Сигнализирует о проблеме при сохранении задач в файл в FileBackedTaskManager
public class ManagerSaveException extends RuntimeException {
    // Конструктор класса ManagerSaveException
    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
