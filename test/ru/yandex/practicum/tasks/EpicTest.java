package ru.yandex.practicum.tasks;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
        // Проверка на попытку добавления самого себя в качестве подзадачи
        Epic epic = new Epic(1, "Epic", "description", TaskStatus.NEW, List.of(1, 10));
        //testEpic.addSubtask(testEpic.getID());
        assertFalse(epic.getSubtaskIDs().contains(epic.getID()),
                "Эпик нельзя добавить в самого себя в виде подзадачи");
    }

    @Test
    void shouldNotAddNullAsSubtask() {
        // Проверяем на добавление null как подзадачи
        ArrayList<Integer> subtasks = new ArrayList<>();
        subtasks.add(10);
        subtasks.add(null);
        Epic epic = new Epic(1, "Epic", "description", TaskStatus.NEW, subtasks);
        //testEpic.addSubtask(10);
        //testEpic.addSubtask(null);
        assertEquals(1, epic.getSubtaskIDs().size(), "null нельзя добавить как подзадачу");
    }
}