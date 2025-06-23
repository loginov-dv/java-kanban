package ru.yandex.practicum.exceptions;

// Сигнализирует о проблеме при загрузке задач из файла в FileBackedTaskManager
public class ManagerLoadException extends RuntimeException {
    // Конструктор класса ManagerLoadException
    public ManagerLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
