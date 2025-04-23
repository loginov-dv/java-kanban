package ru.yandex.practicum.tasks;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SubtaskTest {
    private static Subtask testSubtask;

    @BeforeAll
    static void beforeAll() {
        testSubtask = new Subtask(1, "Subtask1", "description of subtask1",
                TaskStatus.NEW, 1000);
    }

    @Test
    void checkIfTwoSubtasksWithTheSameIdEquals() {
        Subtask anotherSubtask = new Subtask(1, "Subtask2", "description of subtask2",
                TaskStatus.IN_PROGRESS, 6000);

        assertEquals(testSubtask, anotherSubtask, "Подзадачи не равны");
    }

    @Test
    void shouldNotAddItselfAsEpic() {
        assertNotNull(testSubtask.getEpicID(), "Начальный id эпика не был задан");

        testSubtask.setEpicID(testSubtask.getId());
        assertNotEquals(testSubtask.getId(), testSubtask.getEpicID(),
                "Подзадача не может быть своим же эпиком");
    }
}