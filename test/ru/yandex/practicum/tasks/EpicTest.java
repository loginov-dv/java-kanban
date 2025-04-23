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
    void checkIfTwoEpicsWithTheSameIdEquals() {
        Epic anotherEpic = new Epic(1, "Epic2", "description of epic2", TaskStatus.IN_PROGRESS);

        assertEquals(testEpic, anotherEpic, "Задачи не равны");
    }

    @Test
    void shouldNotAddItselfAsSubtask() {
        testEpic.addSubtask(testEpic.getId());
        assertFalse(testEpic.getSubtaskIDs().contains(testEpic.getId()),
                "Эпик не может быть добавлен в свой список подзадач");
    }

    @Test
    void shouldNotAddNullAsSubtask() {
        assertEquals(0, testEpic.getSubtaskIDs().size(), "Список подзадач не был пуст");

        testEpic.addSubtask(10);
        testEpic.addSubtask(null);
        assertEquals(1, testEpic.getSubtaskIDs().size(), "null нельзя добавить как подзадачу");
    }
}