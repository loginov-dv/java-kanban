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
    void checkEquals() {
        // Две задачи должны быть равны, если равны их id
        // Создаём ещё одну подзадачу с таким же id, но отличными значениями других полей
        Subtask anotherSubtask = new Subtask(1, "Subtask2", "description of subtask2",
                TaskStatus.IN_PROGRESS, 6000);

        assertEquals(testSubtask, anotherSubtask, "Задачи с одинаковыми id оказались не равны");
    }

    @Test
    void shouldNotAddItselfAsEpic() {
        // Подзадачу нельзя сделать своим же эпиком
        testSubtask.setEpicID(testSubtask.getId());
        assertNotEquals(testSubtask.getId(), testSubtask.getEpicID(),
                "Подзадачу нельзя сделать своим же эпиком");
    }
}