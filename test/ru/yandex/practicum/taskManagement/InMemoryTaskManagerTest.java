package ru.yandex.practicum.taskManagement;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.yandex.practicum.tasks.*;

class InMemoryTaskManagerTest {
    private static TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void canAddTasks() {
        // Создаём задачу (обычную)
        Task task = new Task(1, "task", "description", TaskStatus.NEW);
        taskManager.addBasicTask(task);

        // Проверяем добавление задачи (обычной)
        assertEquals(1, taskManager.getAllBasicTasks().size(), "Задача не была добавлена в трекер");
    }

    @Test
    void canAddEpics() {
        // Создаём эпик
        Epic epic = new Epic(1, "epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic);

        // Проверяем добавление эпика
        assertEquals(1, taskManager.getAllEpics().size(), "Эпик не был добавлен в трекер");
    }

    @Test
    void canAddSubtasks() {
        // Создаём подзадачу
        Subtask subtask = new Subtask(1, "subtask", "description", TaskStatus.NEW, 10);
        taskManager.addSubtask(subtask);

        // Проверяем добавление подзадачи
        assertEquals(1, taskManager.getAllSubtasks().size(), "Подзадача не была добавлена в трекер");
    }

    @Test
    void canFindTask() {
        // Создаём задачу
        Task task = new Task(1, "task", "description", TaskStatus.NEW);
        taskManager.addBasicTask(task);

        // Проверяем получение задачи (обычной) по id
        Task taskFoundInManager = taskManager.getBasicTaskById(task.getID());
        assertNotNull(taskFoundInManager, "В трекере не найдена добавленная задача");

        // Дополнительная проверка на попытку получение задачи, которой нет в трекере
        assertNull(taskManager.getBasicTaskById(1000), "В трекере найдена несуществующая задача");
    }

    @Test
    void canFindEpic() {
        // Создаём эпик
        Epic epic = new Epic(1, "epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic);

        // Проверяем получение эпика по id
        Epic epicFoundInManager = taskManager.getEpicById(epic.getID());
        assertNotNull(epicFoundInManager, "В трекере не найден добавленный эпик");

        // Дополнительная проверка на попытку получение эпика, которого нет в трекере
        assertNull(taskManager.getEpicById(1000), "В трекере найден несуществующий эпик");
    }

    @Test
    void canFindSubtask() {
        // Создаём подзадачу
        Subtask subtask = new Subtask(1, "subtask", "description", TaskStatus.NEW, 10);
        taskManager.addSubtask(subtask);

        // Проверяем получение подзадачи по id
        Subtask subtaskFoundInManager = taskManager.getSubtaskById(subtask.getID());
        assertNotNull(subtaskFoundInManager, "В трекере не найдена добавленная подзадача");

        // Дополнительная проверка на попытку получение подзадачи, которой нет в трекере
        assertNull(taskManager.getSubtaskById(1000), "В трекере найдена несуществующая подзадача");
    }

    @Test
    void shouldUpdateEpicIfAddedSubtask() {
        // Создаём эпик
        Epic epic1 = new Epic(1, "Эпик 1", "Описание", TaskStatus.NEW);
        taskManager.addEpic(epic1);

        // Создаём подзадачу, которая ссылается на этот эпик
        Subtask subtask11 = new Subtask(11, "Подзадача 11", "Описание",
                TaskStatus.NEW, epic1.getID());
        taskManager.addSubtask(subtask11);

        // Получаем эпик
        Epic updatedEpic = taskManager.getEpicById(epic1.getID());
        assertEquals(1, updatedEpic.getSubtaskIDs().size(),
                "id подзадачи не был добавлен в список подзадач эпика");
    }

    @Test
    void shouldChangeEpicStatusIfSubtaskChangeTheirStatus() {
        // Создаём эпик
        Epic epic = new Epic(1, "Epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic);

        // Создаём подзадачи
        Subtask sub1 = new Subtask(10, "Subtask1", "description", TaskStatus.NEW, epic.getID());
        taskManager.addSubtask(sub1);
        Subtask sub2 = new Subtask(20, "Subtask2", "description", TaskStatus.NEW, epic.getID());
        taskManager.addSubtask(sub2);

        // Проверяем, что id подзадач добавились к эпику
        epic = taskManager.getEpicById(epic.getID());
        assertArrayEquals(epic.getSubtaskIDs().toArray(), List.of(sub1.getID(), sub2.getID()).toArray(),
                "id подзадач не были добавлены в список подзадач эпика");

        // Дополнительно проверяем, что у подзадач заполнено поле с id эпика
        sub2 = taskManager.getSubtaskById(sub2.getID());
        assertEquals(epic.getID(), sub2.getEpicID(), "id эпика не был добавлен в подзадачу");

        // Обновляем статус одной из подзадач (NEW -> DONE)
        taskManager.updateSubtask(new Subtask(sub1.getID(), sub1.getName(), sub1.getDescription(),
                TaskStatus.DONE, epic.getID()));

        // Статус эпика должен измениться на IN_PROGRESS
        epic = taskManager.getEpicById(epic.getID());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Некорректное обновление статуса у эпика");

        // Обновляем статус второй подзадачи (NEW -> DONE)
        taskManager.updateSubtask(new Subtask(sub2.getID(), sub2.getName(), sub2.getDescription(),
                TaskStatus.DONE, epic.getID()));

        // Статус эпика должен измениться на DONE
        epic = taskManager.getEpicById(epic.getID());
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
        epic = taskManager.getEpicById(epic.getID());

        // Проверяем добавление задачи в историю
        assertEquals(1, taskManager.getHistory().size(), "Задача не была добавлена в историю");

        // Проверяем, что это действительно та задача
        assertTrue(taskManager.getHistory().contains(epic),
                "В историю была добавлена задача с некорректным id");

        // Создаём подзадачи
        Subtask sub1 = new Subtask(10, "Subtask1", "description", TaskStatus.NEW, epic.getID());
        taskManager.addSubtask(sub1);
        Subtask sub2 = new Subtask(20, "Subtask2", "description", TaskStatus.NEW, epic.getID());
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

        // Обновляем задачу путём передачи нового объекта (с изменённым состоянием)
        Task updatedTask = new Task(task.getID(), "Task1_upd", "description_upd", TaskStatus.IN_PROGRESS);
        taskManager.updateBasicTask(updatedTask);

        // Проверяем изменение полей
        task = taskManager.getBasicTaskById(task.getID());
        assertEquals(updatedTask.getName(), task.getName(), "Не было обновлено имя задачи");
        assertEquals(updatedTask.getDescription(), task.getDescription(), "Не было обновлено описание задачи");
        assertEquals(updatedTask.getStatus(), task.getStatus(), "Не был обновлен статус задачи");
    }


}