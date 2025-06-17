package ru.yandex.practicum.tasks;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

class EpicTest {
    @Test
    void checkEquals() {
        // Две задачи должны быть равны, если равны их id
        // Создаём два эпика с одинаковым id, но отличными значениями других полей
        Epic epic1 = new Epic(1, "Epic1", "description of epic1", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(60));
        Epic epic2 = new Epic(1, "Epic2", "description of epic2", TaskStatus.IN_PROGRESS,
                LocalDateTime.now(), Duration.ofMinutes(60));

        assertEquals(epic1, epic2, "Задачи с одинаковыми id оказались не равны");
    }

    @Test
    void shouldNotAddItselfAsSubtask() {
        // Проверяем на попытку добавления самого себя в качестве подзадачи
        Epic epic = new Epic(1, "Epic", "description", TaskStatus.NEW,
                new ArrayList<>(Arrays.asList(10, 1)),
                LocalDateTime.now(), Duration.ofMinutes(60));

        assertFalse(epic.getSubtaskIDs().contains(epic.getID()),
                "Эпик нельзя добавить в самого себя в виде подзадачи");
    }

    @Test
    void shouldNotAddNullAsSubtask() {
        // Проверяем на попытку добавление null как подзадачи
        Epic epic = new Epic(1, "Epic", "description", TaskStatus.NEW,
                new ArrayList<>(Arrays.asList(10, null)),
                LocalDateTime.now(), Duration.ofMinutes(60));

        assertEquals(1, epic.getSubtaskIDs().size(), "null нельзя добавить как подзадачу");
    }
}