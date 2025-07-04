package ru.yandex.practicum.tasks;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class TaskTest {
    @Test
    public void checkEquals() {
        // Две задачи должны быть равны, если равны их id
        // Создаём две задачи с одинаковыми id, но с разными значениями других полей
        Task task1 = new Task(1, "Task1", "description of task1", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(60));
        Task task2 = new Task(1, "Task2", "description of task2", TaskStatus.IN_PROGRESS,
                LocalDateTime.now(), Duration.ofMinutes(60));

        assertEquals(task1, task2, "Задачи с одинаковыми id оказались не равны");
    }
}