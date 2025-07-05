package ru.yandex.practicum.exceptions;

// Возникает, когда в TaskManager не найдена запрашиваемая задача
public class TaskNotFoundException extends RuntimeException {
    // Конструктор класса TaskNotFoundException
    public TaskNotFoundException(String message) {
        super(message);
    }

    // Конструктор класса TaskNotFoundException
    public TaskNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
