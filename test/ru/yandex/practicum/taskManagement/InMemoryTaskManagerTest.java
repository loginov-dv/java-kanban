package ru.yandex.practicum.taskManagement;

import java.util.ArrayList;
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
    void shouldAddTasks() {
        // Создаём задачу (обычную)
        Task task = new Task(1, "task", "description", TaskStatus.NEW);
        taskManager.addBasicTask(task);

        // Проверяем добавление задачи (обычной)
        assertEquals(1, taskManager.getAllBasicTasks().size(), "Задача не была добавлена в трекер");
    }

    @Test
    void shouldAddEpics() {
        // Создаём эпик
        Epic epic = new Epic(1, "epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic);

        // Проверяем добавление эпика
        assertEquals(1, taskManager.getAllEpics().size(), "Эпик не был добавлен в трекер");
    }

    @Test
    void shouldAddSubtasks() {
        // Создаём подзадачу
        Subtask subtask = new Subtask(1, "subtask", "description", TaskStatus.NEW, 10);
        taskManager.addSubtask(subtask);

        // Проверяем добавление подзадачи
        assertEquals(1, taskManager.getAllSubtasks().size(), "Подзадача не была добавлена в трекер");
    }

    @Test
    void shouldFindTask() {
        // Создаём задачу и добавляем в трекер
        Task task = new Task(1, "task", "description", TaskStatus.NEW);
        taskManager.addBasicTask(task);

        // Проверяем получение задачи (обычной) по id
        Task taskFoundInManager = taskManager.getBasicTaskById(task.getID());
        assertNotNull(taskFoundInManager, "В трекере не найдена добавленная задача");

        // Дополнительная проверка на попытку получение задачи, которой нет в трекере
        assertNull(taskManager.getBasicTaskById(1000), "В трекере найдена несуществующая задача");
    }

    @Test
    void shouldFindEpic() {
        // Создаём эпик и добавляем в трекер
        Epic epic = new Epic(1, "epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic);

        // Проверяем получение эпика по id
        Epic epicFoundInManager = taskManager.getEpicById(epic.getID());
        assertNotNull(epicFoundInManager, "В трекере не найден добавленный эпик");

        // Дополнительная проверка на попытку получение эпика, которого нет в трекере
        assertNull(taskManager.getEpicById(1000), "В трекере найден несуществующий эпик");
    }

    @Test
    void shouldFindSubtask() {
        // Создаём подзадачу и добавляем в трекер
        Subtask subtask = new Subtask(1, "subtask", "description", TaskStatus.NEW, 10);
        taskManager.addSubtask(subtask);

        // Проверяем получение подзадачи по id
        Subtask subtaskFoundInManager = taskManager.getSubtaskById(subtask.getID());
        assertNotNull(subtaskFoundInManager, "В трекере не найдена добавленная подзадача");

        // Дополнительная проверка на попытку получение подзадачи, которой нет в трекере
        assertNull(taskManager.getSubtaskById(1000), "В трекере найдена несуществующая подзадача");
    }

    @Test
    void shouldUpdateTask() {
        // Создаём задачу и добавляем в трекер
        Task task = new Task(1, "Task1", "description", TaskStatus.NEW);
        taskManager.addBasicTask(task);

        // Обновляем задачу путём передачи нового объекта (с изменённым состоянием)
        Task updatedTask = new Task(task.getID(), "Task1_upd", "description_upd", TaskStatus.IN_PROGRESS);
        taskManager.updateBasicTask(updatedTask);

        // Проверяем изменение полей задачи
        task = taskManager.getBasicTaskById(task.getID());
        assertEquals(updatedTask.getName(), task.getName(), "Не было обновлено имя задачи");
        assertEquals(updatedTask.getDescription(), task.getDescription(), "Не было обновлено описание задачи");
        assertEquals(updatedTask.getStatus(), task.getStatus(), "Не был обновлен статус задачи");
    }

    @Test
    void shouldRemoveTask() {
        // Создаём задачу и добавляем в трекер
        Task task = new Task(1, "Task1", "description", TaskStatus.NEW);
        taskManager.addBasicTask(task);

        // Проверяем, что задача добавлена
        assertEquals(1, taskManager.getAllBasicTasks().size(), "Задача не была добавлена");

        // Удаляем задачу
        taskManager.removeBasicTaskById(1);
        // Проверяем удаление
        assertEquals(0, taskManager.getAllBasicTasks().size(), "Задача не была удалена");
    }

    @Test
    void shouldUpdateEpicWhenAddedSubtask() {
        // Создаём эпик и добавляем в трекер
        Epic epic1 = new Epic(1, "Эпик 1", "Описание", TaskStatus.NEW);
        taskManager.addEpic(epic1);

        // Создаём подзадачу, которая ссылается на этот эпик
        Subtask subtask11 = new Subtask(11, "Подзадача 11", "Описание",
                TaskStatus.IN_PROGRESS, epic1.getID());
        taskManager.addSubtask(subtask11);

        // Получаем эпик из трекера, ожидая, что он должен обновиться
        Epic updatedEpic = taskManager.getEpicById(epic1.getID());
        assertEquals(1, updatedEpic.getSubtaskIDs().size(),
                "id подзадачи не был добавлен в список подзадач эпика");
        assertEquals(TaskStatus.IN_PROGRESS, updatedEpic.getStatus(),
                "Некорректное обновление статуса эпика");
    }

    @Test
    void shouldUpdateEpicWhenRemovedSubtask() {
        // Создаём эпик и добавляем в трекер
        Epic epic1 = new Epic(1, "Эпик 1", "Описание", TaskStatus.NEW);
        taskManager.addEpic(epic1);

        // Создаём подзадачу, которая ссылается на этот эпик
        Subtask subtask11 = new Subtask(11, "Подзадача 11", "Описание",
                TaskStatus.IN_PROGRESS, epic1.getID());
        taskManager.addSubtask(subtask11);

        // Получаем эпик из трекера, ожидая, что он должен обновиться
        Epic updatedEpic = taskManager.getEpicById(epic1.getID());
        assertEquals(1, updatedEpic.getSubtaskIDs().size(),
                "id подзадачи не был добавлен в список подзадач эпика");
        assertEquals(TaskStatus.IN_PROGRESS, updatedEpic.getStatus(),
                "Некорректное обновление статуса эпика");

        // Удаляем подзадачу из трекера
        taskManager.removeSubtaskById(11);

        // Получаем эпик из трекера, ожидая, что он должен обновиться
        // (кол-во элементов в списке подзадач = 0 и статус = NEW)
        updatedEpic = taskManager.getEpicById(epic1.getID());
        assertEquals(0, updatedEpic.getSubtaskIDs().size(),
                "id подзадачи не был удалён из списка подзадач эпика");
        assertEquals(TaskStatus.NEW, updatedEpic.getStatus(),
                "Некорректное обновление статуса эпика");
    }

    @Test
    void shouldUpdateEpicStatusWhenUpdatedSubtask() {
        // Создаём эпик и добавляем в трекер
        Epic epic = new Epic(1, "Epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic);

        // Создаём подзадачи со ссылкой на эпик
        Subtask sub1 = new Subtask(10, "Subtask1", "description", TaskStatus.NEW, epic.getID());
        taskManager.addSubtask(sub1);
        Subtask sub2 = new Subtask(20, "Subtask2", "description", TaskStatus.NEW, epic.getID());
        taskManager.addSubtask(sub2);

        // Получаем эпик из трекера, ожидая, что он должен обновиться
        epic = taskManager.getEpicById(epic.getID());
        assertArrayEquals(epic.getSubtaskIDs().toArray(), List.of(sub1.getID(), sub2.getID()).toArray(),
                "id подзадач не были добавлены в список подзадач эпика");

        // Дополнительно проверяем, что у подзадач (одной из) заполнено поле с id эпика
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
    void shouldUpdateEpicsWhenRemovedAllSubtasks() {
        // Создаём эпики и добавляем в трекер
        Epic epic1 = new Epic(1, "Эпик 1", "Описание", TaskStatus.NEW);
        Epic epic2 = new Epic(2, "Эпик 2", "Описание", TaskStatus.NEW);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        // Создаём подзадачи и добавляем в трекер
        Subtask subtask11 = new Subtask(11, "Подзадача 11", "Описание",
                TaskStatus.IN_PROGRESS, epic1.getID());
        Subtask subtask21 = new Subtask(21, "Подзадача 21", "Описание",
                TaskStatus.DONE, epic2.getID());
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask21);

        // Получаем эпики из трекера, ожидая, что они должны обновиться
        epic1 = taskManager.getEpicById(epic1.getID());
        assertEquals(1, epic1.getSubtaskIDs().size(),
                "id подзадачи не был добавлен в список подзадач эпика");
        epic2 = taskManager.getEpicById(epic1.getID());
        assertEquals(1, epic2.getSubtaskIDs().size(),
                "id подзадачи не был добавлен в список подзадач эпика");

        // Удаляем все подзадачи
        taskManager.removeAllSubtasks();

        // Получаем эпики из трекера, ожидая, что они должны обновиться
        epic1 = taskManager.getEpicById(epic1.getID());
        assertEquals(0, epic1.getSubtaskIDs().size(),
                "id подзадачи не был удалён из списка подзадач эпика");
        assertEquals(TaskStatus.NEW, epic1.getStatus(),
                "status эпика не был обновлён при удалении всех его подзадач");
        epic2 = taskManager.getEpicById(epic1.getID());
        assertEquals(0, epic2.getSubtaskIDs().size(),
                "id подзадачи не был удалён из списка подзадач эпика");
        assertEquals(TaskStatus.NEW, epic2.getStatus(),
                "status эпика не был обновлён при удалении всех его подзадач");
    }

    @Test
    void shouldUpdateSubtasksWhenRemovedAllEpics() {
        // Создаём эпики и добавляем в трекер
        Epic epic1 = new Epic(1, "Эпик 1", "Описание", TaskStatus.NEW);
        Epic epic2 = new Epic(2, "Эпик 2", "Описание", TaskStatus.NEW);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        // Создаём подзадачи и добавляем в трекер
        Subtask subtask11 = new Subtask(11, "Подзадача 11", "Описание",
                TaskStatus.NEW, epic1.getID());
        Subtask subtask21 = new Subtask(21, "Подзадача 21", "Описание",
                TaskStatus.NEW, epic2.getID());
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask21);

        // Получаем эпики из трекера, ожидая, что они должны обновиться
        epic1 = taskManager.getEpicById(epic1.getID());
        assertEquals(1, epic1.getSubtaskIDs().size(),
                "id подзадачи не был добавлен в список подзадач эпика");
        epic2 = taskManager.getEpicById(epic1.getID());
        assertEquals(1, epic2.getSubtaskIDs().size(),
                "id подзадачи не был добавлен в список подзадач эпика");

        // Удаляем все эпики
        taskManager.removeAllEpics();

        // Получаем подзадачи из трекера, ожидая, что они должны обновиться
        subtask11 = taskManager.getSubtaskById(11);
        assertNull(subtask11.getEpicID(), "id эпика не был удалён из подзадачи");
        subtask21 = taskManager.getSubtaskById(21);
        assertNull(subtask21.getEpicID(), "id эпика не был удалён из подзадачи");
    }

    @Test
    void shouldAddSingleTaskToHistory() {
        // Создаём эпик и добавляем в трекер
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

        // Создаём подзадачи со ссылкой на id эпика
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
    void shouldAddMultipleTasksToHistory() {
        // Создаём эпик
        Epic epic = new Epic(10, "epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic);

        // Создаём подзадачу
        Subtask subtask = new Subtask(100, "subtask", "description",
                TaskStatus.NEW, 10);
        taskManager.addSubtask(subtask);

        // Проверим, что размер списка истории соответствует количеству вызовов методов получения задач
        taskManager.getEpicById(10);
        taskManager.getSubtaskById(100);
        assertEquals(2, taskManager.getHistory().size(),
                "Количество задач в истории просмотра не соответствует количеству вызовов методов для " +
                        "получения задач");
        // Проверим состав списка
        ArrayList<Integer> expectedList = new ArrayList<>(List.of(10, 100));
        ArrayList<Integer> actualList = new ArrayList<>();
        for (Task task : taskManager.getHistory()) {
            actualList.add(task.getID());
        }
        assertArrayEquals(expectedList.toArray(), actualList.toArray(),
                "Некорректные задачи в истории просмотра задач");
    }

    @Test
    void shouldNotAddNullToHistory() {
        // Проверяем, что история не содержит задач
        assertEquals(0, taskManager.getHistory().size(), "История была не пуста");

        // Пытаемся получить задачу, которой нету в трекере (менеджер в этом тесте в принципе не содержит задач)
        taskManager.getBasicTaskById(999);

        // Проверяем, что история по-прежнему пуста
        assertEquals(0, taskManager.getHistory().size(),
                "Некорректное добавление задач в историю просмотра");
    }

    @Test
    void historyShouldBeImmutable() {
        // Создадим несколько задач и добавим в трекер
        Task task1 = new Task(1, "Task1", "description", TaskStatus.NEW);
        Task task2 = new Task(2, "Task2", "description", TaskStatus.IN_PROGRESS);
        taskManager.addBasicTask(task1);
        taskManager.addBasicTask(task2);

        // Получим задачи по id
        taskManager.getBasicTaskById(1);
        taskManager.getBasicTaskById(2);

        // Проверим, что задачи попали в историю
        assertEquals(2, taskManager.getHistory().size(),
                "Некорректное добавление задач в историю просмотра");

        // Получим историю просмотра и очистим её
        List<Task> history = taskManager.getHistory();
        history.clear();

        // Проверим, что это действие никак не отразилось на истории в трекере задач
        assertEquals(2, taskManager.getHistory().size(),
                "Некорректное добавление задач в историю просмотра");
    }

    @Test
    void singleTaskShouldBeRemovedFromHistoryWhenRemovedFromManager() {
        // Создадим несколько задач и добавим в трекер
        Task task1 = new Task(1, "Task1", "description", TaskStatus.NEW);
        Task task2 = new Task(2, "Task2", "description", TaskStatus.IN_PROGRESS);
        taskManager.addBasicTask(task1);
        taskManager.addBasicTask(task2);

        // Получим задачи по id
        taskManager.getBasicTaskById(1);
        taskManager.getBasicTaskById(2);

        // Проверим, что задачи попали в историю
        assertEquals(2, taskManager.getHistory().size(),
                "Некорректное добавление задач в историю просмотра");

        // Удалим задачу из трекера
        taskManager.removeBasicTaskById(1);

        // Проверим корректность удаления из истории (осталась только одна задача с id = 2)
        assertEquals(1, taskManager.getHistory().size(),
                "Некорректное удаление задачи из истории при удалении из менеджера");
        assertFalse(taskManager.getHistory().contains(task1),
                "Некорректное удаление задачи из истории при удалении из менеджера");
    }

    @Test
    void multipleTasksShouldBeRemovedFromHistoryWhenRemovedFromManager() {
        // Создадим несколько задач и добавим в трекер
        Task task1 = new Task(1, "Task1", "description", TaskStatus.NEW);
        Task task2 = new Task(2, "Task2", "description", TaskStatus.IN_PROGRESS);
        taskManager.addBasicTask(task1);
        taskManager.addBasicTask(task2);
        // Создадим эпик и добавим в трекер
        Epic epic = new Epic(10, "epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic);

        // Получим задачи и эпик по id
        taskManager.getBasicTaskById(1);
        taskManager.getBasicTaskById(2);
        taskManager.getEpicById(10);

        // Проверим, что задачи попали в историю
        assertEquals(3, taskManager.getHistory().size(),
                "Некорректное добавление задач в историю просмотра");

        // Удалим все задачи (обычные)
        taskManager.removeAllBasicTasks();

        // Проверим корректность удаления из истории (должен остаться только эпик)
        assertEquals(1, taskManager.getHistory().size(),
                "Некорректное удаление задач из истории просмотра");
        assertTrue(taskManager.getHistory().contains(epic),
                "История просмотра не содержит необходимую задачу");
    }

    @Test
    void shouldKeepOnlyRecentViewOfTaskInHistoryWhenUpdatedTask() {
        // Создадим несколько задач и добавим в трекер
        Task task1 = new Task(1, "Task1", "description", TaskStatus.NEW);
        Task task2 = new Task(2, "Task2", "description", TaskStatus.IN_PROGRESS);
        taskManager.addBasicTask(task1);
        taskManager.addBasicTask(task2);

        // Получим задачи по id
        taskManager.getBasicTaskById(1);
        taskManager.getBasicTaskById(2);

        // Проверим корректность добавления задач в историю
        assertEquals(2, taskManager.getHistory().size(),
                "Некорректное количество задач в истории просмотра");
        assertTrue(taskManager.getHistory().contains(task1),
                "История не содержит задачу с id = 1");
        assertTrue(taskManager.getHistory().contains(task2),
                "История не содержит задачу с id = 2");

        // Изменим поле description у задачи с id = 1 и обновим её в трекере
        Task task1_upd = new Task(task1.getID(), "Task1", "new description", TaskStatus.NEW);
        taskManager.updateBasicTask(task1_upd);

        // Проверим, что в истории хранится предыдущее состояние задачи с id = 1 (задача не запрашивалась на просмотр)
        for (Task task : taskManager.getHistory()) {
            if (task.getID() == task1.getID()) {
                assertEquals("description", task.getDescription(),
                        "Некорректное состояние задачи с id = 1 в истории просмотра");
            }
        }

        // Получим задачу с id = 1
        taskManager.getBasicTaskById(1);

        // Проверим, что в истории теперь хранится новое состояние задачи с id = 1
        for (Task task : taskManager.getHistory()) {
            if (task.getID() == task1.getID()) {
                assertEquals("new description", task.getDescription(),
                        "Некорректное состояние задачи с id = 1 в истории просмотра");
            }
        }
    }

    @Test
    void shouldKeepOnlyRecentViewOfTaskInHistoryWhenTaskViewedMultipleTimes() {
        // Создадим несколько задач и добавим в трекер
        Task task1 = new Task(1, "Task1", "description", TaskStatus.NEW);
        Task task2 = new Task(2, "Task2", "description", TaskStatus.IN_PROGRESS);
        taskManager.addBasicTask(task1);
        taskManager.addBasicTask(task2);

        // Получим задачи по id
        taskManager.getBasicTaskById(1);
        taskManager.getBasicTaskById(2);
        // Ещё несколько раз запросим задачу с id = 1
        taskManager.getBasicTaskById(1);
        taskManager.getBasicTaskById(1);

        // Проверим, что задачи попали в историю
        // В истории должно быть всего две задачи, несмотря на то, что
        // одна из задач была запрошена несколько раз
        assertEquals(2, taskManager.getHistory().size(),
                "Некорректное добавление задач в историю просмотра");
    }

    @Test
    void shouldRemoveTaskFromMiddleOfHistory() {
        // Создадим несколько задач и добавим в трекер
        Task task1 = new Task(1, "Task1", "description", TaskStatus.NEW);
        Task task2 = new Task(2, "Task2", "description", TaskStatus.IN_PROGRESS);
        Task task3 = new Task(3, "Task3", "description", TaskStatus.DONE);
        Task task4 = new Task(4, "Task4", "description", TaskStatus.NEW);
        Task task5 = new Task(5, "Task5", "description", TaskStatus.IN_PROGRESS);
        taskManager.addBasicTask(task1);
        taskManager.addBasicTask(task2);
        taskManager.addBasicTask(task3);
        taskManager.addBasicTask(task4);
        taskManager.addBasicTask(task5);

        // Запросим все задачи
        taskManager.getBasicTaskById(task1.getID());
        taskManager.getBasicTaskById(task2.getID());
        taskManager.getBasicTaskById(task3.getID());
        taskManager.getBasicTaskById(task4.getID());
        taskManager.getBasicTaskById(task5.getID());

        // Проверим состав списка
        ArrayList<Integer> expectedList = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        ArrayList<Integer> actualList = new ArrayList<>();
        for (Task task : taskManager.getHistory()) {
            actualList.add(task.getID());
        }
        assertArrayEquals(expectedList.toArray(), actualList.toArray(),
                "Некорректный состав или порядок задач в истории просмотра");

        // Удалим задачу с id = 3 (в середине списка истории задач) из трекера
        taskManager.removeBasicTaskById(task3.getID());

        // Проверим, что задачи с id = 3 больше нет в истории
        assertFalse(taskManager.getHistory().contains(task3),
                "Задача с id = 3 была удалена из трекера, но по-прежнему присутствует в истории");

        // Проверим состав списка
        expectedList = new ArrayList<>(List.of(1, 2, 4, 5));
        actualList.clear();
        actualList = new ArrayList<>();
        for (Task task : taskManager.getHistory()) {
            actualList.add(task.getID());
        }
        assertArrayEquals(expectedList.toArray(), actualList.toArray(),
                "Некорректный состав или порядок задач в истории просмотра");
    }

    @Test
    void shouldRemoveTaskFromEndOfHistory() {
        // Создадим несколько задач и добавим в трекер
        Task task1 = new Task(1, "Task1", "description", TaskStatus.NEW);
        Task task2 = new Task(2, "Task2", "description", TaskStatus.IN_PROGRESS);
        Task task3 = new Task(3, "Task3", "description", TaskStatus.DONE);
        Task task4 = new Task(4, "Task4", "description", TaskStatus.NEW);
        Task task5 = new Task(5, "Task5", "description", TaskStatus.IN_PROGRESS);
        taskManager.addBasicTask(task1);
        taskManager.addBasicTask(task2);
        taskManager.addBasicTask(task3);
        taskManager.addBasicTask(task4);
        taskManager.addBasicTask(task5);

        // Запросим все задачи
        taskManager.getBasicTaskById(task1.getID());
        taskManager.getBasicTaskById(task2.getID());
        taskManager.getBasicTaskById(task3.getID());
        taskManager.getBasicTaskById(task4.getID());
        taskManager.getBasicTaskById(task5.getID());

        // Проверим состав списка
        ArrayList<Integer> expectedList = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        ArrayList<Integer> actualList = new ArrayList<>();
        for (Task task : taskManager.getHistory()) {
            actualList.add(task.getID());
        }
        assertArrayEquals(expectedList.toArray(), actualList.toArray(),
                "Некорректный состав или порядок задач в истории просмотра");

        // Удалим задачу с id = 5 (в конце списка истории задач) из трекера
        taskManager.removeBasicTaskById(task5.getID());

        // Проверим, что задачи с id = 5 больше нет в истории
        assertFalse(taskManager.getHistory().contains(task5),
                "Задача с id = 5 была удалена из трекера, но по-прежнему присутствует в истории");

        // Проверим состав списка
        expectedList = new ArrayList<>(List.of(1, 2, 3, 4));
        actualList.clear();
        actualList = new ArrayList<>();
        for (Task task : taskManager.getHistory()) {
            actualList.add(task.getID());
        }
        assertArrayEquals(expectedList.toArray(), actualList.toArray(),
                "Некорректный состав или порядок задач в истории просмотра");
    }

}