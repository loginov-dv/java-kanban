package ru.yandex.practicum.exceptions;

public class ManagerSaveException extends RuntimeException {
    // Конструктор класса ManagerSaveException
    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
