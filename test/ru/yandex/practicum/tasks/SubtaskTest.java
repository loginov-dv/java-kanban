package ru.yandex.practicum.tasks;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class SubtaskTest {
    @Test
    void checkEquals() {
        // Две задачи должны быть равны, если равны их id
        // Создаём две подзадачи с одинаковым id, но отличными значениями других полей
        Subtask subtask1 = new Subtask(1, "Subtask1", "description of subtask1",
                TaskStatus.NEW, 1000,
                LocalDateTime.now(), Duration.ofMinutes(60));
        Subtask subtask2 = new Subtask(1, "Subtask2", "description of subtask2",
                TaskStatus.IN_PROGRESS, 6000,
                LocalDateTime.now(), Duration.ofMinutes(60));

        assertEquals(subtask1, subtask2, "Задачи с одинаковыми id оказались не равны");
    }

    @Test
    void shouldNotAddItselfAsEpic() {
        // Подзадачу нельзя сделать своим же эпиком
        Subtask subtask = new Subtask(1, "Subtask", "description", TaskStatus.NEW, 1,
                LocalDateTime.now(), Duration.ofMinutes(60));

        assertNotEquals(subtask.getID(), subtask.getEpicID(),
                "Подзадачу нельзя сделать своим же эпиком");
    }
}