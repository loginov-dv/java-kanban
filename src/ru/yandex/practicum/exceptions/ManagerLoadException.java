package ru.yandex.practicum.exceptions;

public class ManagerLoadException extends RuntimeException {
    // Конструктор класса ManagerLoadException
    public ManagerLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
