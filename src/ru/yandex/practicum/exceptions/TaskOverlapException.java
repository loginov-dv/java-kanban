package ru.yandex.practicum.exceptions;

// Возникает, когда в TaskManager добавляется задача, имеющая
// пересечение по времени выполнения с другой задачей, уже присутствующей в трекере
public class TaskOverlapException extends RuntimeException {
    // Конструктор класса TaskOverlapException
    public TaskOverlapException(String message, Throwable cause) {
        super(message, cause);
    }

  // Конструктор класса TaskOverlapException
    public TaskOverlapException(String message) {
        super(message);
    }
}
