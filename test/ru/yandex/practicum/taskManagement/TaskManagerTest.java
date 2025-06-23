package ru.yandex.practicum.taskManagement;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exceptions.TaskOverlapException;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

// Абстрактный класс, содержащий тесты для методов интерфейса TaskManager
// Если при работе с Optional опущен isPresent(), значит значение гарантированно присутствует
public abstract class TaskManagerTest<T extends TaskManager> {
    // Экземпляр класса TaskManager (будет создаваться в наследниках)
    protected T taskManager;

    // Абстрактный метод для инициализации taskManager (реализуется в наследниках)
    @BeforeEach
    protected abstract void beforeEach();

    // Проверяет добавление задачи в трекер
    @Test
    void shouldAddTask() {
        // Создаём задачу (обычную)
        Task task = new Task(1, "task", "description", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(60));
        taskManager.addBasicTask(task);

        // Проверяем добавление задачи (обычной)
        assertEquals(1, taskManager.getAllBasicTasks().size(), "Задача не была добавлена в трекер");
    }

    // Проверяет добавление эпика в трекер
    @Test
    void shouldAddEpic() {
        // Создаём эпик
        Epic epic = new Epic(1, "epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic);

        // Проверяем добавление эпика
        assertEquals(1, taskManager.getAllEpics().size(), "Эпик не был добавлен в трекер");
    }

    // Проверяет добавление подзадачи в трекер
    @Test
    void shouldAddSubtask() {
        // Создаём подзадачу
        Subtask subtask = new Subtask(1, "subtask", "description", TaskStatus.NEW, 10,
                LocalDateTime.now(), Duration.ofMinutes(60));
        taskManager.addSubtask(subtask);

        // Проверяем добавление подзадачи
        assertEquals(1, taskManager.getAllSubtasks().size(), "Подзадача не была добавлена в трекер");
    }

    // Проверяет поиск задачи по id в трекере
    @Test
    void shouldFindTask() {
        // Создаём задачу и добавляем в трекер
        Task task = new Task(1, "task", "description", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(60));
        taskManager.addBasicTask(task);

        // Проверяем получение задачи (обычной) по id
        Optional<Task> foundTask = taskManager.getBasicTaskById(task.getID());
        assertFalse(foundTask.isEmpty(), "В трекере не найдена добавленная задача");
    }

    // Проверяет поиск эпика по id в трекере
    @Test
    void shouldFindEpic() {
        // Создаём эпик и добавляем в трекер
        Epic epic = new Epic(1, "epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic);

        // Проверяем получение эпика по id
        Optional<Epic> foundEpic = taskManager.getEpicById(epic.getID());
        assertFalse(foundEpic.isEmpty(), "В трекере не найден добавленный эпик");
    }

    // Проверяет поиск подзадачи по id в трекере
    @Test
    void shouldFindSubtask() {
        // Создаём подзадачу и добавляем в трекер
        Subtask subtask = new Subtask(1, "subtask", "description", TaskStatus.NEW, 10,
                LocalDateTime.now(), Duration.ofMinutes(60));
        taskManager.addSubtask(subtask);

        // Проверяем получение подзадачи по id
        Optional<Subtask> foundSubtask = taskManager.getSubtaskById(subtask.getID());
        assertFalse(foundSubtask.isEmpty(), "В трекере не найдена добавленная подзадача");
    }

    // Проверяет попытку получения задачи, которой нет в трекере
    @Test
    void shouldNotFindTaskThatDoesntExist() {
        assertTrue(taskManager.getBasicTaskById(1000).isEmpty(), "В трекере найдена несуществующая задача");
    }

    // Проверяет попытку получения эпика, которого нет в трекере
    @Test
    void shouldNotFindEpicThatDoesntExist() {
        assertTrue(taskManager.getEpicById(1000).isEmpty(), "В трекере найден несуществующий эпик");
    }

    // Проверяет попытку получения подзадачи, которой нет в трекере
    @Test
    void shouldNotFindSubtaskThatDoesntExist() {
        assertTrue(taskManager.getSubtaskById(1000).isEmpty(), "В трекере найдена несуществующая подзадача");
    }

    // Проверяет обновление задачи в трекере
    @Test
    void shouldUpdateTask() {
        // Создаём задачу и добавляем в трекер
        Task task = new Task(1, "Task1", "description", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(60));
        taskManager.addBasicTask(task);

        // Обновляем задачу путём передачи нового объекта (с изменённым состоянием)
        Task updatedTask = new Task(task.getID(), "Task1_upd", "description_upd", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 10, 12, 0), Duration.ofMinutes(100));
        taskManager.updateBasicTask(updatedTask);

        // Проверяем изменение полей задачи
        task = taskManager.getBasicTaskById(task.getID()).get();
        assertEquals(updatedTask.getName(), task.getName(),
                "Не было обновлено имя задачи");
        assertEquals(updatedTask.getDescription(), task.getDescription(),
                "Не было обновлено описание задачи");
        assertEquals(updatedTask.getStatus(), task.getStatus(),
                "Не был обновлен статус задачи");
        assertEquals(updatedTask.getStartTime(), task.getStartTime(),
                "Не была обновлена дата и время начала задачи");
        assertEquals(updatedTask.getDuration(), task.getDuration(),
                "Не была обновлена продолжительность задачи");
    }

    // Проверяет удаление задачи из трекера
    @Test
    void shouldRemoveTask() {
        // Создаём задачу и добавляем в трекер
        Task task = new Task(1, "Task1", "description", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(60));
        taskManager.addBasicTask(task);

        // Проверяем, что задача добавлена
        assertEquals(1, taskManager.getAllBasicTasks().size(), "Задача не была добавлена");

        // Удаляем задачу
        taskManager.removeBasicTaskById(1);
        // Проверяем удаление
        assertEquals(0, taskManager.getAllBasicTasks().size(), "Задача не была удалена");
    }

    // Проверяет обновление статуса эпика при добавлении нескольких подзадач со статусом NEW
    @Test
    void shouldUpdateEpicStatusWhenAddedNewSubtasks() {
        // Создадим эпик и добавляем в трекер
        Epic epic1 = new Epic(1, "Эпик 1", "Описание", TaskStatus.NEW);
        taskManager.addEpic(epic1);

        // Создадим подзадачу со статусом NEW
        Subtask subtask11 = new Subtask(11, "Подзадача 11", "Описание",
                TaskStatus.NEW, epic1.getID(),
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));
        taskManager.addSubtask(subtask11);

        // Получим эпик из трекера, ожидая, что он должен обновиться
        Epic updatedEpic = taskManager.getEpicById(epic1.getID()).get();
        assertEquals(1, updatedEpic.getSubtaskIDs().size(),
                "id подзадачи не был добавлен в список подзадач эпика");
        assertEquals(TaskStatus.NEW, updatedEpic.getStatus(),
                "Некорректное обновление статуса эпика");

        // Создадим ещё одну подзадачу со статусом NEW
        Subtask subtask12 = new Subtask(12, "Подзадача 12", "Описание",
                TaskStatus.NEW, epic1.getID(),
                LocalDateTime.of(2025, 1, 2, 10, 0), Duration.ofMinutes(60));
        taskManager.addSubtask(subtask12);

        // Получим эпик из трекера, ожидая, что он должен обновиться
        updatedEpic = taskManager.getEpicById(epic1.getID()).get();
        assertEquals(2, updatedEpic.getSubtaskIDs().size(),
                "id подзадачи не был добавлен в список подзадач эпика");
        assertEquals(TaskStatus.NEW, updatedEpic.getStatus(),
                "Некорректное обновление статуса эпика");
    }

    // Проверяет обновление статуса эпика при удалении подзадачи
    @Test
    void shouldUpdateEpicStatusWhenRemovedSubtask() {
        // Создаём эпик и добавляем в трекер
        Epic epic1 = new Epic(1, "Эпик 1", "Описание", TaskStatus.NEW);
        taskManager.addEpic(epic1);

        // Создаём подзадачу, которая ссылается на этот эпик
        Subtask subtask11 = new Subtask(11, "Подзадача 11", "Описание",
                TaskStatus.IN_PROGRESS, epic1.getID(),
                LocalDateTime.now(), Duration.ofMinutes(60));
        taskManager.addSubtask(subtask11);

        // Получаем эпик из трекера, ожидая, что он должен обновиться
        Epic updatedEpic = taskManager.getEpicById(epic1.getID()).get();
        assertEquals(1, updatedEpic.getSubtaskIDs().size(),
                "id подзадачи не был добавлен в список подзадач эпика");
        assertEquals(TaskStatus.IN_PROGRESS, updatedEpic.getStatus(),
                "Некорректное обновление статуса эпика");

        // Удаляем подзадачу из трекера
        taskManager.removeSubtaskById(11);

        // Получаем эпик из трекера, ожидая, что он должен обновиться
        // (кол-во элементов в списке подзадач = 0 и статус = NEW)
        updatedEpic = taskManager.getEpicById(epic1.getID()).get();
        assertEquals(0, updatedEpic.getSubtaskIDs().size(),
                "id подзадачи не был удалён из списка подзадач эпика");
        assertEquals(TaskStatus.NEW, updatedEpic.getStatus(),
                "Некорректное обновление статуса эпика");
    }

    // Проверяет обновление статуса эпика при различном изменении статусов его подзадач
    @Test
    void shouldUpdateEpicStatusWhenUpdatedSubtasks() {
        // Создаём эпик и добавляем в трекер
        Epic epic = new Epic(1, "Epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic);

        // Создаём подзадачи со ссылкой на эпик
        Subtask sub1 = new Subtask(10, "Subtask1", "description", TaskStatus.NEW, epic.getID(),
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));
        taskManager.addSubtask(sub1);
        Subtask sub2 = new Subtask(20, "Subtask2", "description", TaskStatus.NEW, epic.getID(),
                LocalDateTime.of(2025, 1, 10, 10, 0), Duration.ofMinutes(60));
        taskManager.addSubtask(sub2);

        // Получаем эпик из трекера, ожидая, что он должен обновиться
        epic = taskManager.getEpicById(epic.getID()).get();
        assertArrayEquals(epic.getSubtaskIDs().toArray(), List.of(sub1.getID(), sub2.getID()).toArray(),
                "id подзадач не были добавлены в список подзадач эпика");

        // Дополнительно проверяем, что у подзадач (одной из) заполнено поле с id эпика
        sub2 = taskManager.getSubtaskById(sub2.getID()).get();
        assertEquals(epic.getID(), sub2.getEpicID(), "id эпика не был добавлен в подзадачу");

        // Обновляем статус первой подзадачи (NEW -> IN_PROGRESS)
        taskManager.updateSubtask(new Subtask(sub1.getID(), sub1.getName(), sub1.getDescription(),
                TaskStatus.IN_PROGRESS, epic.getID(),
                sub1.getStartTime().get(), sub1.getDuration()));

        // Статус эпика должен измениться на IN_PROGRESS
        epic = taskManager.getEpicById(epic.getID()).get();
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Некорректное обновление статуса у эпика");

        // Обновляем статус первой подзадачи (IN_PROGRESS -> DONE)
        taskManager.updateSubtask(new Subtask(sub1.getID(), sub1.getName(), sub1.getDescription(),
                TaskStatus.DONE, epic.getID(),
                sub1.getStartTime().get(), sub1.getDuration()));

        // Статус эпика должен остаться IN_PROGRESS
        epic = taskManager.getEpicById(epic.getID()).get();
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Некорректное обновление статуса у эпика");

        // Обновляем статус второй подзадачи (NEW -> IN_PROGRESS)
        taskManager.updateSubtask(new Subtask(sub2.getID(), sub2.getName(), sub2.getDescription(),
                TaskStatus.IN_PROGRESS, epic.getID(),
                sub2.getStartTime().get(), sub2.getDuration()));

        // Статус эпика должен остаться IN_PROGRESS
        epic = taskManager.getEpicById(epic.getID()).get();
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Некорректное обновление статуса у эпика");

        // Обновляем статус второй подзадачи (IN_PROGRESS -> DONE)
        taskManager.updateSubtask(new Subtask(sub2.getID(), sub2.getName(), sub2.getDescription(),
                TaskStatus.DONE, epic.getID(),
                sub2.getStartTime().get(), sub2.getDuration()));

        // Статус эпика должен измениться на DONE
        epic = taskManager.getEpicById(epic.getID()).get();
        assertEquals(TaskStatus.DONE, epic.getStatus(), "Некорректное обновление статуса у эпика");
    }

    // Проверяет, что при удалении эпика удаляются также и его подзадачи
    @Test
    void shouldRemoveSubtasksWhenRemovedEpic() {
        // Создаём эпики и добавляем в трекер
        Epic epic1 = new Epic(1, "Epic", "description", TaskStatus.NEW);
        Epic epic2 = new Epic(2, "Epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        // Создаём подзадачи со ссылкой на эпик
        Subtask sub11 = new Subtask(11, "Subtask11", "description", TaskStatus.NEW, epic1.getID(),
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));
        taskManager.addSubtask(sub11);
        Subtask sub12 = new Subtask(12, "Subtask12", "description", TaskStatus.NEW, epic1.getID(),
                LocalDateTime.of(2025, 1, 10, 10, 0), Duration.ofMinutes(60));
        taskManager.addSubtask(sub12);
        Subtask sub21 = new Subtask(21, "Subtask21", "description", TaskStatus.NEW, epic2.getID(),
                LocalDateTime.of(2025, 1, 20, 10, 0), Duration.ofMinutes(60));
        taskManager.addSubtask(sub21);

        // Удаляем эпик с id = 1
        taskManager.removeEpicById(epic1.getID());

        // Проверяем, что также удалились и его подзадачи
        assertFalse(taskManager.getAllSubtasks().contains(sub11),
                "Подзадача не удалилась из трекера при удалении её эпика");
        assertFalse(taskManager.getAllSubtasks().contains(sub12),
                "Подзадача не удалилась из трекера при удалении её эпика");

        // Проверим, что подзадача эпика с id = 2 не удалилась
        assertTrue(taskManager.getAllSubtasks().contains(sub21),
                "Подзадача была излишне удалена из трекера");
    }

    // Проверяет обновление статусов эпиков при удалении всех подзадач
    @Test
    void shouldUpdateEpicStatusWhenRemovedAllSubtasks() {
        // Создаём эпики и добавляем в трекер
        Epic epic1 = new Epic(1, "Эпик 1", "Описание", TaskStatus.NEW);
        Epic epic2 = new Epic(2, "Эпик 2", "Описание", TaskStatus.NEW);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        // Создаём подзадачи и добавляем в трекер
        Subtask subtask11 = new Subtask(11, "Подзадача 11", "Описание",
                TaskStatus.IN_PROGRESS, epic1.getID(),
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));
        Subtask subtask21 = new Subtask(21, "Подзадача 21", "Описание",
                TaskStatus.DONE, epic2.getID(),
                LocalDateTime.of(2025, 1, 10, 10, 0), Duration.ofMinutes(60));
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask21);

        // Получаем эпики из трекера, ожидая, что они должны обновиться
        epic1 = taskManager.getEpicById(epic1.getID()).get();
        assertEquals(1, epic1.getSubtaskIDs().size(),
                "id подзадачи не был добавлен в список подзадач эпика");
        epic2 = taskManager.getEpicById(epic2.getID()).get();
        assertEquals(1, epic2.getSubtaskIDs().size(),
                "id подзадачи не был добавлен в список подзадач эпика");

        // Удаляем все подзадачи
        taskManager.removeAllSubtasks();

        // Получаем эпики из трекера, ожидая, что они должны обновиться
        epic1 = taskManager.getEpicById(epic1.getID()).get();
        assertEquals(0, epic1.getSubtaskIDs().size(),
                "id подзадачи не был удалён из списка подзадач эпика");
        assertEquals(TaskStatus.NEW, epic1.getStatus(),
                "status эпика не был обновлён при удалении всех его подзадач");
        epic2 = taskManager.getEpicById(epic2.getID()).get();
        assertEquals(0, epic2.getSubtaskIDs().size(),
                "id подзадачи не был удалён из списка подзадач эпика");
        assertEquals(TaskStatus.NEW, epic2.getStatus(),
                "status эпика не был обновлён при удалении всех его подзадач");
    }

    // Проверяет, что при удалении всех эпиков удаляются все подзадачи
    @Test
    void shouldRemoveSubtasksWhenRemovedAllEpics() {
        // Создаём эпики и добавляем в трекер
        Epic epic1 = new Epic(1, "Epic", "description", TaskStatus.NEW);
        Epic epic2 = new Epic(2, "Epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        // Создаём подзадачи со ссылкой на эпик
        Subtask sub11 = new Subtask(11, "Subtask11", "description", TaskStatus.NEW, epic1.getID(),
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));
        taskManager.addSubtask(sub11);
        Subtask sub12 = new Subtask(12, "Subtask12", "description", TaskStatus.NEW, epic1.getID(),
                LocalDateTime.of(2025, 1, 10, 10, 0), Duration.ofMinutes(60));
        taskManager.addSubtask(sub12);
        Subtask sub21 = new Subtask(21, "Subtask21", "description", TaskStatus.NEW, epic2.getID(),
                LocalDateTime.of(2025, 1, 10, 20, 0), Duration.ofMinutes(60));
        taskManager.addSubtask(sub21);

        // Удаляем все эпики
        taskManager.removeAllEpics();

        // Проверяем, что подзадачи тоже удалились
        assertEquals(0, taskManager.getAllSubtasks().size(), "Подзадачи не были удалены из трекера");
    }

    // Проверяет добавление задачи (одной) в историю
    @Test
    void shouldAddSingleTaskToHistory() {
        // Создаём эпик и добавляем в трекер
        Epic epic = new Epic(1, "Epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic);

        // Проверяем, что история пуста
        assertEquals(0, taskManager.getHistory().size(),
                "Методы получения задач по id не вызывались, но история просмотра содержит задачи");

        // Запрашиваем задачу по id
        epic = taskManager.getEpicById(epic.getID()).get();

        // Проверяем добавление задачи в историю
        assertEquals(1, taskManager.getHistory().size(), "Задача не была добавлена в историю");

        // Проверяем, что это действительно та задача
        assertTrue(taskManager.getHistory().contains(epic),
                "В историю была добавлена задача с некорректным id");
    }

    // Проверяет добавление нескольких задач в историю
    @Test
    void shouldAddMultipleTasksToHistory() {
        // Создаём эпик
        Epic epic = new Epic(10, "epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic);

        // Создаём подзадачу
        Subtask subtask = new Subtask(100, "subtask", "description",
                TaskStatus.NEW, 10,
                LocalDateTime.now(), Duration.ofMinutes(60));
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

    // Проверяет, что в историю не должен добавляться null
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

    // Проверяет, что изменение запрошенного списка истории задач не влияет на историю в трекере
    @Test
    void historyShouldBeImmutable() {
        // Создадим несколько задач и добавим в трекер
        Task task1 = new Task(1, "Task1", "description", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));
        Task task2 = new Task(2, "Task2", "description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, 1, 10, 10, 0), Duration.ofMinutes(60));
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

    // Проверяет, что задача (одна) удаляется из истории при удалении из трекера
    @Test
    void singleTaskShouldBeRemovedFromHistoryWhenRemovedFromManager() {
        // Создадим несколько задач и добавим в трекер
        Task task1 = new Task(1, "Task1", "description", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));
        Task task2 = new Task(2, "Task2", "description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, 1, 10, 10, 0), Duration.ofMinutes(60));
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

    // Проверяет, что несколько задач удаляются из истории при удалении из трекера
    @Test
    void multipleTasksShouldBeRemovedFromHistoryWhenRemovedFromManager() {
        // Создадим несколько задач и добавим в трекер
        Task task1 = new Task(1, "Task1", "description", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));
        Task task2 = new Task(2, "Task2", "description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, 1, 10, 10, 0), Duration.ofMinutes(60));
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

    // Проверяет, что в истории хранится предыдущее состояние задачи
    @Test
    void shouldKeepOnlyRecentViewOfTaskInHistoryWhenUpdatedTask() {
        // Создадим несколько задач и добавим в трекер
        Task task1 = new Task(1, "Task1", "description", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));
        Task task2 = new Task(2, "Task2", "description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, 1, 10, 10, 0), Duration.ofMinutes(60));
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
        Task task1_upd = new Task(task1.getID(), "Task1", "new description", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(60));
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

    // Проверяет, что история не содержит дубликатов задач
    @Test
    void shouldKeepOnlyRecentViewOfTaskInHistoryWhenTaskViewedMultipleTimes() {
        // Создадим несколько задач и добавим в трекер
        Task task1 = new Task(1, "Task1", "description", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));
        Task task2 = new Task(2, "Task2", "description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, 1, 10, 10, 0), Duration.ofMinutes(60));
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

    // Проверяет удаление задачи из середины списка истории
    @Test
    void shouldRemoveTaskFromMiddleOfHistory() {
        // Создадим несколько задач и добавим в трекер
        Task task1 = new Task(1, "Task1", "description", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));
        Task task2 = new Task(2, "Task2", "description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, 1, 2, 10, 0), Duration.ofMinutes(60));
        Task task3 = new Task(3, "Task3", "description", TaskStatus.DONE,
                LocalDateTime.of(2025, 1, 3, 10, 0), Duration.ofMinutes(60));
        Task task4 = new Task(4, "Task4", "description", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 4, 10, 0), Duration.ofMinutes(60));
        Task task5 = new Task(5, "Task5", "description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, 1, 5, 10, 0), Duration.ofMinutes(60));
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

    // Проверяет удаление задачи с конца списка истории
    @Test
    void shouldRemoveTaskFromEndOfHistory() {
        // Создадим несколько задач и добавим в трекер
        Task task1 = new Task(1, "Task1", "description", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));
        Task task2 = new Task(2, "Task2", "description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, 1, 2, 10, 0), Duration.ofMinutes(60));
        Task task3 = new Task(3, "Task3", "description", TaskStatus.DONE,
                LocalDateTime.of(2025, 1, 3, 10, 0), Duration.ofMinutes(60));
        Task task4 = new Task(4, "Task4", "description", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 4, 10, 0), Duration.ofMinutes(60));
        Task task5 = new Task(5, "Task5", "description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, 1, 5, 10, 0), Duration.ofMinutes(60));
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

    // Проверяет, что при удалении эпика из трекера, он также удаляется и из истории вместе с его подзадачами
    @Test
    void shouldRemoveEpicFromHistoryWithItsSubtasksWhenRemovedFromManager() {
        // Создаём эпики и добавляем в трекер
        Epic epic1 = new Epic(1, "Epic", "description", TaskStatus.NEW);
        Epic epic2 = new Epic(2, "Epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        // Создаём подзадачи со ссылкой на эпик
        Subtask sub11 = new Subtask(11, "Subtask11", "description", TaskStatus.NEW, epic1.getID(),
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));
        taskManager.addSubtask(sub11);
        Subtask sub12 = new Subtask(12, "Subtask12", "description", TaskStatus.NEW, epic1.getID(),
                LocalDateTime.of(2025, 1, 10, 10, 0), Duration.ofMinutes(60));
        taskManager.addSubtask(sub12);
        Subtask sub21 = new Subtask(21, "Subtask21", "description", TaskStatus.NEW, epic2.getID(),
                LocalDateTime.of(2025, 1, 20, 10, 0), Duration.ofMinutes(60));
        taskManager.addSubtask(sub21);

        // Запросим эпики и подзадачи
        taskManager.getEpicById(epic1.getID());
        taskManager.getSubtaskById(sub11.getID());
        taskManager.getSubtaskById(sub21.getID());
        taskManager.getSubtaskById(sub12.getID());
        taskManager.getEpicById(epic2.getID());

        // Проверим, что эпик с id = 1 и его подзадачи попали в историю
        assertTrue(taskManager.getHistory().contains(epic1), "Эпик отсутствует в истории просмотра задач");
        assertTrue(taskManager.getHistory().contains(sub11), "Подзадача отсутствует в истории просмотра задач");
        assertTrue(taskManager.getHistory().contains(sub12), "Подзадача отсутствует в истории просмотра задач");

        // Удалим эпик с id = 1
        taskManager.removeEpicById(epic1.getID());

        // Проверим, что эпика с id = 1 и его подзадач больше нет в истории
        assertFalse(taskManager.getHistory().contains(epic1),
                "Эпик присутствует в истории просмотра задач, несмотря на то, что был удалён из трекера");
        assertFalse(taskManager.getHistory().contains(sub11),
                "Подзадача присутствует в истории просмотра задач, несмотря на то, " +
                        "что связанный эпик был удалён из трекера");
        assertFalse(taskManager.getHistory().contains(sub12),
                "Подзадача присутствует в истории просмотра задач, несмотря на то, " +
                        "что связанный эпик был удалён из трекера");
    }

    // Проверяет изменение временных характеристик эпика при добавлении подзадач
    @Test
    void shouldUpdateEpicTimeParamsWhenAddedSubtasks() {
        // Создадим эпик и добавляем в трекер
        Epic epic = new Epic(1, "Epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic);

        // Создадим подзадачу
        Subtask sub1 = new Subtask(10, "Subtask1", "description", TaskStatus.NEW, epic.getID(),
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));
        taskManager.addSubtask(sub1);

        // Получаем эпик из трекера, ожидая, что он должен обновиться
        // Т.к. подзадача одна, то дата и время начала эпика, а также его продолжительность,
        // равны соответствующим характеристикам его единственной подзадачи
        epic = taskManager.getEpicById(epic.getID()).get();
        assertEquals(sub1.getStartTime(), epic.getStartTime(),
                "Некорректное определение даты и времени начала эпика");
        assertEquals(sub1.getDuration(), epic.getDuration(),
                "Некорректное определение продолжительности эпика");
        assertEquals(sub1.getEndTime(), epic.getEndTime(),
                "Некорректное определение даты окончания эпика");

        // Добавим ещё одну подзадачу, которая начинается после первой подзадачи
        Subtask sub2 = new Subtask(11, "Subtask1", "description", TaskStatus.NEW, epic.getID(),
                LocalDateTime.of(2025, 3, 1, 10, 0), Duration.ofMinutes(120));
        taskManager.addSubtask(sub2);

        // Получаем эпик из трекера, ожидая, что он должен обновиться
        // Дата и время начала равны дате и времени начала первой подзадачи
        // Продолжительность равна сумме продолжительностей обеих подзадач
        // Дата и время окончания равны дате и времени окончания второй подзадачи (т.к. она позднее)
        epic = taskManager.getEpicById(epic.getID()).get();
        assertEquals(sub1.getStartTime(), epic.getStartTime(),
                "Некорректное определение даты и времени начала эпика");
        assertEquals(sub1.getDuration().plus(sub2.getDuration()), epic.getDuration(),
                "Некорректное определение продолжительности эпика");
        assertEquals(sub2.getEndTime(), epic.getEndTime(),
                "Некорректное определение даты окончания эпика");

        // Добавим ещё одну задачу, выполнение которой происходит между первой и второй подзадачей
        // Дата и время начала/окончания эпика не изменятся, но изменится его продолжительность
        Subtask sub3 = new Subtask(12, "Subtask1", "description", TaskStatus.NEW, epic.getID(),
                LocalDateTime.of(2025, 2, 1, 10, 0), Duration.ofMinutes(180));
        taskManager.addSubtask(sub3);

        // Получаем эпик из трекера, ожидая, что он должен обновиться
        epic = taskManager.getEpicById(epic.getID()).get();
        assertEquals(sub1.getStartTime(), epic.getStartTime(),
                "Некорректное определение даты и времени начала эпика");
        assertEquals(sub1.getDuration().plus(sub2.getDuration()).plus(sub3.getDuration()), epic.getDuration(),
                "Некорректное определение продолжительности эпика");
        assertEquals(sub2.getEndTime(), epic.getEndTime(),
                "Некорректное определение даты окончания эпика");
    }

    // Проверяет изменение временных характеристик эпика при удалении подзадач
    @Test
    void shouldUpdateEpicTimeParamsWhenRemovedSubtask() {
        // Создадим эпик
        Epic epic = new Epic(1, "Эпик 1", "Описание", TaskStatus.NEW);
        taskManager.addEpic(epic);

        // Создадим подзадачу
        Subtask subtask = new Subtask(11, "Подзадача 11", "Описание",
                TaskStatus.IN_PROGRESS, epic.getID(),
                LocalDateTime.now(), Duration.ofMinutes(60));
        taskManager.addSubtask(subtask);

        // Получаем эпик из трекера, ожидая, что он должен обновиться
        Epic updatedEpic = taskManager.getEpicById(epic.getID()).get();
        assertEquals(subtask.getStartTime(), updatedEpic.getStartTime(),
                "Некорректное определение даты и времени начала эпика");
        assertEquals(subtask.getDuration(), updatedEpic.getDuration(),
                "Некорректное определение продолжительности эпика");
        assertEquals(subtask.getEndTime(), updatedEpic.getEndTime(),
                "Некорректное определение даты окончания эпика");

        // Удаляем подзадачу из трекера
        taskManager.removeSubtaskById(subtask.getID());

        // Получаем эпик из трекера, ожидая, что он должен обновиться
        // (startTime = null, duration = ZERO)
        updatedEpic = taskManager.getEpicById(epic.getID()).get();
        assertEquals(Optional.empty(), updatedEpic.getStartTime(), "Некорректное определение даты и времени начала эпика");
        assertEquals(Duration.ZERO, updatedEpic.getDuration(),
                "Некорректное определение продолжительности эпика");
        assertEquals(Optional.empty(), updatedEpic.getEndTime(), "Некорректное определение даты окончания эпика");
    }

    // Проверяет обновление временных характеристик эпика при изменении временных характеристик его подзадач
    @Test
    void shouldUpdateEpicTimeParamsWhenUpdatedSubtasks() {
        // Создаём эпик
        Epic epic = new Epic(1, "Epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic);

        // Создаём подзадачи
        Subtask sub1 = new Subtask(10, "Subtask1", "description", TaskStatus.NEW, epic.getID(),
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));
        taskManager.addSubtask(sub1);
        Subtask sub2 = new Subtask(11, "Subtask1", "description", TaskStatus.NEW, epic.getID(),
                LocalDateTime.of(2025, 3, 1, 10, 0), Duration.ofMinutes(120));
        taskManager.addSubtask(sub2);

        // Получаем эпик из трекера, ожидая, что он должен обновиться
        // Дата и время начала равны дате и времени начала первой подзадачи
        // Продолжительность равна сумме продолжительностей обеих подзадач
        // Дата и время окончания равны дате и времени окончания второй подзадачи (т.к. она позднее)
        epic = taskManager.getEpicById(epic.getID()).get();
        assertEquals(sub1.getStartTime(), epic.getStartTime(),
                "Некорректное определение даты и времени начала эпика");
        assertEquals(sub1.getDuration().plus(sub2.getDuration()), epic.getDuration(),
                "Некорректное определение продолжительности эпика");
        assertEquals(sub2.getEndTime(), epic.getEndTime(),
                "Некорректное определение даты окончания эпика");

        // Обновляем продолжительность первой подзадачи (60 -> 10)
        sub1 = new Subtask(sub1.getID(), sub1.getName(), sub1.getDescription(),
                TaskStatus.IN_PROGRESS, epic.getID(),
                sub1.getStartTime().get(), Duration.ofMinutes(10));
        taskManager.updateSubtask(sub1);

        // Проверяем изменение временных параметров эпика
        epic = taskManager.getEpicById(epic.getID()).get();
        assertEquals(sub1.getStartTime(), epic.getStartTime(),
                "Некорректное определение даты и времени начала эпика");
        assertEquals(sub1.getDuration().plus(sub2.getDuration()), epic.getDuration(),
                "Некорректное определение продолжительности эпика");
        assertEquals(sub2.getEndTime(), epic.getEndTime(),
                "Некорректное определение даты окончания эпика");

        // Обновляем дату и время начала первой подзадачи (10:00 01.01.2025 -> 08:00 01.03.2025)
        // Она всё ещё начинается раньше второй подзадачи
        sub1 = new Subtask(sub1.getID(), sub1.getName(), sub1.getDescription(),
                TaskStatus.DONE, epic.getID(),
                LocalDateTime.of(2025, 3, 1, 8, 0), sub1.getDuration());
        taskManager.updateSubtask(sub1);

        // Проверяем изменение временных параметров эпика
        epic = taskManager.getEpicById(epic.getID()).get();
        assertEquals(sub1.getStartTime(), epic.getStartTime(),
                "Некорректное определение даты и времени начала эпика");
        assertEquals(sub1.getDuration().plus(sub2.getDuration()), epic.getDuration(),
                "Некорректное определение продолжительности эпика");
        assertEquals(sub2.getEndTime(), epic.getEndTime(),
                "Некорректное определение даты окончания эпика");
    }

    // Проверяет обновление временных характеристик эпиков при удалении всех подзадач
    @Test
    void shouldUpdateEpicTimeParamsWhenRemovedAllSubtasks() {
        // Создаём эпики и добавляем в трекер
        Epic epic1 = new Epic(1, "Эпик 1", "Описание", TaskStatus.NEW);
        Epic epic2 = new Epic(2, "Эпик 2", "Описание", TaskStatus.NEW);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        // Создаём подзадачи и добавляем в трекер
        Subtask subtask11 = new Subtask(11, "Подзадача 11", "Описание",
                TaskStatus.IN_PROGRESS, epic1.getID(),
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));
        Subtask subtask21 = new Subtask(21, "Подзадача 21", "Описание",
                TaskStatus.DONE, epic2.getID(),
                LocalDateTime.of(2025, 1, 10, 10, 0), Duration.ofMinutes(60));
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask21);

        // Получаем эпики из трекера, ожидая, что они должны обновиться
        epic1 = taskManager.getEpicById(epic1.getID()).get();
        assertEquals(subtask11.getStartTime(), epic1.getStartTime(),
                "Некорректное определение даты и времени начала эпика");
        assertEquals(subtask11.getDuration(), epic1.getDuration(),
                "Некорректное определение продолжительности эпика");
        assertEquals(subtask11.getEndTime(), epic1.getEndTime(),
                "Некорректное определение даты окончания эпика");
        epic2 = taskManager.getEpicById(epic2.getID()).get();
        assertEquals(subtask21.getStartTime(), epic2.getStartTime(),
                "Некорректное определение даты и времени начала эпика");
        assertEquals(subtask21.getDuration(), epic2.getDuration(),
                "Некорректное определение продолжительности эпика");
        assertEquals(subtask21.getEndTime(), epic2.getEndTime(),
                "Некорректное определение даты окончания эпика");

        // Удаляем все подзадачи
        taskManager.removeAllSubtasks();

        // Получаем эпики из трекера, ожидая, что они должны обновиться
        epic1 = taskManager.getEpicById(epic1.getID()).get();
        assertEquals(Optional.empty(), epic1.getStartTime(), "Некорректное определение даты и времени начала эпика");
        assertEquals(Duration.ZERO, epic1.getDuration(),
                "Некорректное определение продолжительности эпика");
        assertEquals(Optional.empty(), epic1.getEndTime(), "Некорректное определение даты окончания эпика");
        epic2 = taskManager.getEpicById(epic2.getID()).get();
        assertEquals(Optional.empty(), epic2.getStartTime(), "Некорректное определение даты и времени начала эпика");
        assertEquals(Duration.ZERO, epic2.getDuration(),
                "Некорректное определение продолжительности эпика");
        assertEquals(Optional.empty(), epic2.getEndTime(), "Некорректное определение даты окончания эпика");
    }

    // Проверяет возможные случаи пересечения задач по времени выполнения
    @Test
    void shouldNotAddTaskThatHasOverlapWithOtherTasks() {
        // Создадим задачу и добавим её в трекер
        Task task1 = new Task(1, "1", "description", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 0, 0), Duration.ofMinutes(60));
        taskManager.addBasicTask(task1);

        // Создадим задачу, которая имеет пересечение с уже существующей задачей
        // -----*========*---------- task1
        // ----------*========*----- task2
        Task task2 = new Task(2, "2", "description", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 0, 30), Duration.ofMinutes(60));
        assertThrows(TaskOverlapException.class, () -> taskManager.addBasicTask(task2),
                "Задача, имеющая пересечение с уже существующей задачей, была некорректно добавлена в трекер");

        // Создадим задачу, которая имеет пересечение с уже существующей задачей
        // ----------*========*----- task1
        // -----*========*---------- task3
        Task task3 = new Task(3, "3", "description", TaskStatus.NEW,
                LocalDateTime.of(2024, 12, 31, 23, 30), Duration.ofMinutes(60));
        assertThrows(TaskOverlapException.class, () -> taskManager.addBasicTask(task3),
                "Задача, имеющая пересечение с уже существующей задачей, была некорректно добавлена в трекер");

        // Создадим задачу, которая имеет пересечение с уже существующей задачей
        // ----------*========*---------- task1
        // -----*==================*----- task4
        Task task4 = new Task(4, "4", "description", TaskStatus.NEW,
                LocalDateTime.of(2024, 12, 31, 23, 30), Duration.ofMinutes(120));
        assertThrows(TaskOverlapException.class, () -> taskManager.addBasicTask(task4),
                "Задача, имеющая пересечение с уже существующей задачей, была некорректно добавлена в трекер");

        // Создадим задачу, которая имеет пересечение с уже существующей задачей
        // -----*========*----- task1
        // -------*====*------- task5
        Task task5 = new Task(5, "5", "description", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 0, 15), Duration.ofMinutes(30));
        assertThrows(TaskOverlapException.class, () -> taskManager.addBasicTask(task5),
                "Задача, имеющая пересечение с уже существующей задачей, была некорректно добавлена в трекер");

        // Создадим задачу, не имеющую пересечений с уже добавленной задачей (одна перетекает в другую)
        // ----------*========*----- task1
        // -----*====*-------------- task6
        Task task6 = new Task(6, "6", "description", TaskStatus.NEW,
                LocalDateTime.of(2024, 12, 31, 23, 30), Duration.ofMinutes(30));
        assertDoesNotThrow(() -> taskManager.addBasicTask(task6),
                "Задача, не имеющая пересечений с уже существующей задачей, не была добавлена в трекер");

        // Создадим задачу, не имеющую пересечений с уже добавленной задачей (одна перетекает в другую)
        // -----*========*---------- task1
        // --------------*====*----- task7
        Task task7 = new Task(7, "7", "description", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 1, 0), Duration.ofMinutes(30));
        assertDoesNotThrow(() -> taskManager.addBasicTask(task7),
                "Задача, не имеющая пересечений с уже существующей задачей, не была добавлена в трекер");

        // Создадим задачу, не имеющую пересечений с уже добавленной задачей
        // ---------------*========*----- task1
        // -----*====*------------------- task8
        Task task8 = new Task(8, "8", "description", TaskStatus.NEW,
                LocalDateTime.of(2024, 12, 31, 20, 0), Duration.ofMinutes(30));
        assertDoesNotThrow(() -> taskManager.addBasicTask(task8),
                "Задача, не имеющая пересечений с уже существующей задачей, не была добавлена в трекер");

        // Создадим задачу, не имеющую пересечений с уже добавленной задачей
        // -----*========*--------------- task1
        // -------------------*====*----- task9
        Task task9 = new Task(9, "9", "description", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 3, 0), Duration.ofMinutes(30));
        assertDoesNotThrow(() -> taskManager.addBasicTask(task9),
                "Задача, не имеющая пересечений с уже существующей задачей, не была добавлена в трекер");
    }
}
