package ru.yandex.practicum.taskManagement;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.tasks.*;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryTaskManagerTest {
    private static TaskManager taskManager;

    @BeforeAll
    static void beforeAll() {
        taskManager = Managers.getDefault();
    }

    @BeforeEach
    void beforeEach() {
        taskManager.removeAllBasicTasks();
        taskManager.removeAllEpics();
        taskManager.removeAllSubtasks();
    }

    @Test
    void canAddTasks() {
        assertEquals(0, taskManager.getAllBasicTasks().size(), "Список задач не был пуст");

        Task task = new Task(1, "task", "description", TaskStatus.NEW);
        taskManager.addBasicTask(task);
        assertEquals(1, taskManager.getAllBasicTasks().size(), "Задача не была добавлена");
    }

    @Test
    void canAddEpics() {
        assertEquals(0, taskManager.getAllEpics().size(), "Список эпиков не был пуст");

        Epic epic = new Epic(1, "epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic);
        assertEquals(1, taskManager.getAllEpics().size(), "Эпик не был добавлен");
    }

    @Test
    void canAddSubtasks() {
        assertEquals(0, taskManager.getAllSubtasks().size(), "Список подзадач не был пуст");

        Subtask subtask = new Subtask(1, "subtask", "description", TaskStatus.NEW, 10);
        taskManager.addSubtask(subtask);
        assertEquals(1, taskManager.getAllSubtasks().size(), "Подзадача не была добавлена");
    }

    @Test
    void canFindTask() {
        assertEquals(0, taskManager.getAllBasicTasks().size(), "Список задач не был пуст");

        Task task = new Task(1, "task", "description", TaskStatus.NEW);
        taskManager.addBasicTask(task);
        Task taskFoundInManager = taskManager.getBasicTaskById(task.getId());
        assertNotNull(taskFoundInManager, "Не найдена добавленная задача");
        assertNull(taskManager.getBasicTaskById(1000), "Найдена несуществующая задача");
    }

    @Test
    void canFindEpic() {
        assertEquals(0, taskManager.getAllEpics().size(), "Список эпиков не был пуст");

        Epic epic = new Epic(1, "epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic);
        Epic epicFoundInManager = taskManager.getEpicById(epic.getId());
        assertNotNull(epicFoundInManager, "Не найден добавленный эпик");
        assertNull(taskManager.getEpicById(1000), "Найден несуществующий эпик");
    }

    @Test
    void canFindSubtask() {
        assertEquals(0, taskManager.getAllSubtasks().size(), "Список подзадач не был пуст");

        Subtask subtask = new Subtask(1, "subtask", "description", TaskStatus.NEW, 10);
        taskManager.addSubtask(subtask);
        Subtask subtaskFoundInManager = taskManager.getSubtaskById(subtask.getId());
        assertNotNull(subtaskFoundInManager, "Не найдена добавленная подзадача");
        assertNull(taskManager.getSubtaskById(1000), "Найдена несуществующая подзадача");
    }

    @Test
    void shouldUpdateEpicIfAddedItsSubtask() {
        Epic epic1 = new Epic(1, "Эпик 1", "Описание", TaskStatus.NEW);
        taskManager.addEpic(epic1);

        Subtask subtask11 = new Subtask(11, "Подзадача 11", "Описание",
                TaskStatus.NEW, epic1.getId());
        taskManager.addSubtask(subtask11);
        Epic updatedEpic = taskManager.getEpicById(epic1.getId());
        assertEquals(1, updatedEpic.getSubtaskIDs().size(),
                "Эпик не обновился при добавлении к нему подзадачи");
    }
}