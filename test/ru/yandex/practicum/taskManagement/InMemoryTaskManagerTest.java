package ru.yandex.practicum.taskManagement;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.yandex.practicum.tasks.*;

class InMemoryTaskManagerTest {
    private static TaskManager taskManager;

    /*@BeforeAll
    static void beforeAll() {
        taskManager = Managers.getDefault();
    }*/

    @BeforeEach
    void beforeEach() {
        /*taskManager.removeAllBasicTasks();
        taskManager.removeAllEpics();
        taskManager.removeAllSubtasks();*/
        taskManager = Managers.getDefault();
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

    @Test
    void epicShouldChangeStatus() {
        // Создаём эпик
        Epic epic = new Epic(1, "Epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic);

        // Создаём подзадачи
        Subtask sub1 = new Subtask(10, "Subtask1", "description", TaskStatus.NEW, epic.getId());
        taskManager.addSubtask(sub1);
        Subtask sub2 = new Subtask(20, "Subtask2", "description", TaskStatus.NEW, epic.getId());
        taskManager.addSubtask(sub2);

        // Проверяем, что id подзадач добавились к эпику
        epic = taskManager.getEpicById(epic.getId());
        assertArrayEquals(epic.getSubtaskIDs().toArray(), List.of(sub1.getId(), sub2.getId()).toArray(),
                "id подзадач не были добавлены в список подзадач эпика");

        // Дополнительно проверяем, что у подзадач заполнено поле с id эпика
        sub2 = taskManager.getSubtaskById(sub2.getId());
        assertEquals(epic.getId(), sub2.getEpicID(), "id эпика не был добавлен в подзадачу");

        // Обновляем статус одной из подзадач (NEW -> DONE)
        taskManager.updateSubtask(new Subtask(sub1.getId(), sub1.getName(), sub1.getDescription(), TaskStatus.DONE, epic.getId()));

        // Статус эпика должен измениться на IN_PROGRESS
        epic = taskManager.getEpicById(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Некорректное обновление статуса у эпика");

        // Обновляем статус второй подзадачи (NEW -> DONE)
        taskManager.updateSubtask(new Subtask(sub2.getId(), sub2.getName(), sub2.getDescription(), TaskStatus.DONE, epic.getId()));

        // Статус эпика должен измениться на DONE
        epic = taskManager.getEpicById(epic.getId());
        assertEquals(TaskStatus.DONE, epic.getStatus(), "Некорректное обновление статуса у эпика");
    }

    @Test
    void shouldAddTaskToHistory() {
        // Создаём эпик
        Epic epic = new Epic(1, "Epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic);

        // Проверяем, что история пуста
        assertEquals(0, taskManager.getHistory().size(),
                "Методы получения задач по id не вызывались, но история просмотра содержит задачи");

        // Запрашиваем задачу по id
        epic = taskManager.getEpicById(epic.getId());

        // Проверяем добавление задачи в историю
        assertEquals(1, taskManager.getHistory().size(), "Задача не была добавлена в историю");

        // Проверяем, что это действительно та задача
        // contains() использует equals()
        assertTrue(taskManager.getHistory().contains(epic),
                "В историю была добавлена задача с некорректным id");

        // Создаём подзадачи
        Subtask sub1 = new Subtask(10, "Subtask1", "description", TaskStatus.NEW, epic.getId());
        taskManager.addSubtask(sub1);
        Subtask sub2 = new Subtask(20, "Subtask2", "description", TaskStatus.NEW, epic.getId());
        taskManager.addSubtask(sub2);

        // В трекере эпик обновляется: добавляются id подзадач в список у эпика
        // При этом getHistory() должен возвращать предыдущее состояние эпика (на момент вызова getEpicById())
        assertEquals(0, ((Epic)taskManager.getHistory().getFirst()).getSubtaskIDs().size(),
                "Эпик в истории просмотра содержит подзадачи, хотя на момент вызова getEpicById() их не было");
    }

    @Test
    void shouldUpdateTask() {
        // Создаём задачу
        Task task = new Task(1, "Task1", "description", TaskStatus.NEW);
        taskManager.addBasicTask(task);

        // Обновляем задачу путём передачи нового объекта (с изменённой характеристикой)
        Task updatedTask = new Task(task.getId(), "Task1_upd", "description_upd", TaskStatus.IN_PROGRESS);
        taskManager.updateBasicTask(updatedTask);

        // Проверяем изменение полей
        task = taskManager.getBasicTaskById(task.getId());
        assertEquals(updatedTask.getName(), task.getName(), "Не было обновлено имя задачи");
        assertEquals(updatedTask.getDescription(), task.getDescription(), "Не было обновлено описание задачи");
        assertEquals(updatedTask.getStatus(), task.getStatus(), "Не был обновлен статус задачи");
    }
}