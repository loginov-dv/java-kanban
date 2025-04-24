package ru.yandex.practicum.tasks;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EpicTest {
    private static Epic testEpic;

    @BeforeEach
    void beforeAll() {
        testEpic = new Epic(1, "Epic1", "description of epic1", TaskStatus.NEW);
    }

    @Test
    void checkEquals() {
        // Две задачи должны быть равны, если равны их id
        // Создаём ещё один эпик с таким же id, но отличными значениями других полей
        Epic anotherEpic = new Epic(1, "Epic2", "description of epic2", TaskStatus.IN_PROGRESS);

        assertEquals(testEpic, anotherEpic, "Задачи с одинаковыми id оказались не равны");
    }

    @Test
    void shouldNotAddItselfAsSubtask() {
        // Эпик нельзя добавить в самого себя в виде подзадачи
        testEpic.addSubtask(testEpic.getId());
        assertFalse(testEpic.getSubtaskIDs().contains(testEpic.getId()),
                "Эпик нельзя добавить в самого себя в виде подзадачи");
    }

    @Test
    void shouldNotAddNullAsSubtask() {
        // Проверяем на добавление null как подзадачи
        testEpic.addSubtask(10);
        testEpic.addSubtask(null);
        assertEquals(1, testEpic.getSubtaskIDs().size(), "null нельзя добавить как подзадачу");
    }
}