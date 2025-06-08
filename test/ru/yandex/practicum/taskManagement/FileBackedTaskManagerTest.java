package ru.yandex.practicum.taskManagement;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import ru.yandex.practicum.tasks.*;

class FileBackedTaskManagerTest {
    // Трекер задач
    private static TaskManager taskManager;

    // Вспомогательные файлы
    private static File saveFile;
    private static File sourceFile;
    private static File emptyFile;
    private static File onlyHeaderFile;

    // Инициализация трекера задач
    @BeforeEach
    void beforeEach() {
        try {
            saveFile = File.createTempFile("testSave", ".csv");
            saveFile.deleteOnExit();
            taskManager = new FileBackedTaskManager(saveFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Создание вспомогательных файлов
    @BeforeAll
    static void beforeAll() {
        // Создаём задачу (обычную)
        Task task = new Task(1, "task", "description", TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 20, 13, 0),
                Duration.ofMinutes(60));
        // Создаём эпик
        Epic epic = new Epic(2, "epic", "description", TaskStatus.NEW,
                LocalDateTime.of(2022, 5, 20, 13, 0),
                Duration.ofMinutes(60));
        // Создаём подзадачу
        Subtask subtask = new Subtask(3, "subtask", "description", TaskStatus.NEW, 2,
                LocalDateTime.of(2022, 5, 20, 13, 0),
                Duration.ofMinutes(60));
        try {
            // Записываем задачи в тестовый временный файл
            sourceFile = File.createTempFile("testSource", ".csv");
            sourceFile.deleteOnExit();
            try (Writer writer = new FileWriter(sourceFile, StandardCharsets.UTF_8)) {
                String content = FileBackedTaskManager.HEADER + "\n" +
                        task + "\n" +
                        epic + "\n" +
                        subtask + "\n";
                writer.write(content);
            }

            // Пустой файл
            emptyFile = File.createTempFile("testEmpty", ".csv");
            emptyFile.deleteOnExit();

            // Файл, содержащий только заголовок
            onlyHeaderFile = File.createTempFile("testOnlyHeader", ".csv");
            onlyHeaderFile.deleteOnExit();
            try (Writer writer = new FileWriter(onlyHeaderFile, StandardCharsets.UTF_8)) {
                writer.write(FileBackedTaskManager.HEADER + "\n");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    // Проверяет добавление задач в трекер и запись их в файл
    @Test
    void shouldAddTasks() {
        // Создаём задачу (обычную)
        Task task = new Task(taskManager.nextId(), "task", "description", TaskStatus.NEW);
        taskManager.addBasicTask(task);
        // Проверяем добавление задачи (обычной)
        assertEquals(1, taskManager.getAllBasicTasks().size(), "Задача не была добавлена в трекер");

        // Создаём эпик
        Epic epic = new Epic(taskManager.nextId(), "epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic);
        // Проверяем добавление эпика
        assertEquals(1, taskManager.getAllEpics().size(), "Эпик не был добавлен в трекер");

        // Создаём подзадачу
        Subtask subtask = new Subtask(taskManager.nextId(), "subtask", "description",
                TaskStatus.NEW, epic.getID());
        taskManager.addSubtask(subtask);
        // Проверяем добавление подзадачи
        assertEquals(1, taskManager.getAllSubtasks().size(), "Подзадача не была добавлена в трекер");

        // Ожидаемое содержимое файла
        String contentExpected = FileBackedTaskManager.HEADER + "\n" +
                task + "\n" +
                epic + "\n" +
                subtask + "\n";

        // Фактическое содержимое файла
        StringBuilder contentFact = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(saveFile, StandardCharsets.UTF_8))) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                contentFact.append(line).append("\n");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        // Проверяем, что задачи записаны корректно
        assertEquals(contentExpected, contentFact.toString(), "Некорректная запись задач в файл");
    }

    // Проверяет обновление задач в трекере и в файле
    @Test
    void shouldUpdateTasks() {
        // Создаём задачу (обычную)
        Task task = new Task(taskManager.nextId(), "task", "description", TaskStatus.NEW);
        taskManager.addBasicTask(task);

        // Создаём эпик
        Epic epic = new Epic(taskManager.nextId(), "epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic);

        // Создаём подзадачу
        Subtask subtask = new Subtask(taskManager.nextId(), "subtask", "description",
                TaskStatus.NEW, epic.getID());
        taskManager.addSubtask(subtask);

        // Изменяем задачу (обычную)
        Task updatedTask = new Task(task.getID(), "updatedTask", "updatedDescription",
                TaskStatus.IN_PROGRESS);
        taskManager.updateBasicTask(updatedTask);

        // Ожидаемое содержимое файла
        String contentExpected = FileBackedTaskManager.HEADER + "\n" +
                updatedTask + "\n" +
                epic + "\n" +
                subtask + "\n";

        // Фактическое содержимое файла
        StringBuilder contentFact = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(saveFile, StandardCharsets.UTF_8))) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                contentFact.append(line).append("\n");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        // Проверяем, что задачи записаны корректно
        assertEquals(contentExpected, contentFact.toString(), "Некорректная запись задач в файл");
    }

    // Проверяет удаление задач из трекера и из файла
    @Test
    void shouldRemoveTasks() {
        // Создаём задачу (обычную)
        Task task = new Task(taskManager.nextId(), "task", "description", TaskStatus.NEW);
        taskManager.addBasicTask(task);

        // Создаём эпик
        Epic epic = new Epic(taskManager.nextId(), "epic", "description", TaskStatus.NEW);
        taskManager.addEpic(epic);

        // Создаём подзадачу
        Subtask subtask = new Subtask(taskManager.nextId(), "subtask", "description",
                TaskStatus.NEW, epic.getID());
        taskManager.addSubtask(subtask);

        // Удаляем задачу (обычную)
        taskManager.removeBasicTaskById(task.getID());

        // Ожидаемое содержимое файла
        String contentExpected = FileBackedTaskManager.HEADER + "\n" +
                epic + "\n" +
                subtask + "\n";

        // Фактическое содержимое файла
        StringBuilder contentFact = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(saveFile, StandardCharsets.UTF_8))) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                contentFact.append(line).append("\n");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        // Проверяем, что задачи записаны корректно
        assertEquals(contentExpected, contentFact.toString(), "Некорректная запись задач в файл");

        // Удаляем эпик, вместе с которым должна удалиться и его подзадача
        taskManager.removeEpicById(epic.getID());

        // Ожидаемое содержимое файла (только заголовок)
        contentExpected = FileBackedTaskManager.HEADER + "\n";

        // Фактическое содержимое файла
        contentFact = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(saveFile, StandardCharsets.UTF_8))) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                contentFact.append(line).append("\n");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        // Проверяем, что задачи записаны корректно
        assertEquals(contentExpected, contentFact.toString(), "Некорректная запись задач в файл");
    }

    // Проверяет восстановление данных трекера из заранее подготовленного файла
    @Test
    void shouldRestoreStateFromFile() {
        FileBackedTaskManager taskManagerFromFile = FileBackedTaskManager.loadFromFile(sourceFile, saveFile);

        // Проверяем добавление задачи (обычной)
        assertEquals(1, taskManagerFromFile.getAllBasicTasks().size(),
                "Задача не была добавлена в трекер");
        // Проверяем добавление эпика
        assertEquals(1, taskManagerFromFile.getAllEpics().size(),
                "Эпик не был добавлен в трекер");
        // Проверяем добавление подзадачи
        assertEquals(1, taskManagerFromFile.getAllSubtasks().size(),
                "Подзадача не была добавлена в трекер");

        // Проверяем поля задачи (обычной)
        Task task = taskManagerFromFile.getBasicTaskById(1);
        assertNotNull(task, "Задача с id = 1 отсутствует в трекере");
        assertEquals("task", task.getName(), "Некорректное наименование задачи с id = 1");
        assertEquals("description", task.getDescription(), "Некорректное описание задачи с id = 1");
        assertEquals(TaskStatus.NEW, task.getStatus(), "Некорректный статус задачи с id = 1");
        // Для подзадачи проверим id эпика
        Subtask subtask = taskManagerFromFile.getSubtaskById(3);
        assertNotNull(subtask, "Поздадача с id = 3 отсутствует в трекере");
        assertEquals(2, subtask.getEpicID(), "Некорректный id эпика у поздадачи с id = 3");
        // Для эпика проверим id его подзадач
        Epic epic = taskManagerFromFile.getEpicById(2);
        assertNotNull(epic, "Эпик с id = 2 отсутствует в трекере");
        assertEquals(1, epic.getSubtaskIDs().size(),
                "Некорректное количество подзадач у эпика с id = 2");
        assertEquals(3, epic.getSubtaskIDs().getFirst(),
                "Некорректный номер подзадачи у эпика с id = 2");
    }

    // Проверяет загрузку и корректную обработку пустого файла в трекер
    @Test
    void shouldHandleEmptyFileWhenRestoreStateFromFile() {
        FileBackedTaskManager taskManagerFromFile = FileBackedTaskManager.loadFromFile(emptyFile, saveFile);

        // Проверяем, что задачи не были добавлены
        assertEquals(0, taskManagerFromFile.getAllBasicTasks().size(),
                "В трекер была добавлена задача, хотя файл был пуст");
        assertEquals(0, taskManagerFromFile.getAllEpics().size(),
                "В трекер была добавлена задача, хотя файл был пуст");
        assertEquals(0, taskManagerFromFile.getAllSubtasks().size(),
                "В трекер была добавлена задача, хотя файл был пуст");
    }

    // Проверяет загрузку и корректную обработку файла, состоящего только из заголовка
    @Test
    void shouldHandleFileWithOnlyHeaderWithoutTasksWhenRestoreStateFromFile() {
        FileBackedTaskManager taskManagerFromFile = FileBackedTaskManager.loadFromFile(onlyHeaderFile, saveFile);

        // Проверяем, что задачи не были добавлены
        assertEquals(0, taskManagerFromFile.getAllBasicTasks().size(),
                "В трекер была добавлена задача, хотя файл был пуст");
        assertEquals(0, taskManagerFromFile.getAllEpics().size(),
                "В трекер была добавлена задача, хотя файл был пуст");
        assertEquals(0, taskManagerFromFile.getAllSubtasks().size(),
                "В трекер была добавлена задача, хотя файл был пуст");
    }

    // Проверяет загрузку и корректную обработку файла, в строковых полях которого содержатся кавычки и/или запятые
    @Test
    void shouldHandleQuotationMarksAndCommasInFileWhenRestoreStateFromFile() {
        // Создаём задачу, одно из полей которой содержит внутренние кавычки
        Task task1 = new Task(taskManager.nextId(), "task1", "\"description\"", TaskStatus.NEW);
        taskManager.addBasicTask(task1);
        // Создаём задачу, одно из полей которой содержит запятую
        Task task2 = new Task(taskManager.nextId(), "task, the second", "description", TaskStatus.NEW);
        taskManager.addBasicTask(task2);
        // Создаём задачу, одно из полей которой содержит и кавычки и запятую
        Task task3 = new Task(taskManager.nextId(), "task3", "\"very\", descriptive", TaskStatus.NEW);
        taskManager.addBasicTask(task3);
        // Создаём задачу, одно из полей которой кавычки, а другое - запятую
        Task task4 = new Task(taskManager.nextId(), "task,4", "\"descr", TaskStatus.NEW);
        taskManager.addBasicTask(task4);

        // Создаём новый трекер из файла другого трекера
        File anotherSaveFile = null;
        try {
            anotherSaveFile = File.createTempFile("test1", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        anotherSaveFile.deleteOnExit();
        FileBackedTaskManager newTaskManager = FileBackedTaskManager.loadFromFile(saveFile, anotherSaveFile);

        // Проверяем, что все задачи загружены
        assertEquals(4, newTaskManager.getAllBasicTasks().size(),
                "Некорректное количество задач в трекере, созданном из файла");

        // Проверяем равенство полей отдельных задач
        for (Task originTask : taskManager.getAllBasicTasks()) {
            Task anotherTask = newTaskManager.getBasicTaskById(originTask.getID());
            assertNotNull(anotherTask, "Не найдена задача с таким id в трекере, созданном из файла");
            assertEquals(originTask.getName(), anotherTask.getName(), "Некорректное наименование задачи");
            assertEquals(originTask.getDescription(), anotherTask.getDescription(),
                    "Некорректное описание задачи");
            assertEquals(originTask.getStatus(), anotherTask.getStatus(), "Некорректный статус задачи");
        }
    }

    // Проверяем, что поле globalId обновляется соответствующим образом при чтении задач из файла, и что при создании
    // новых задач через методы add... не происходит коллизий id задач
    @Test
    void shouldHaveNoIdCollisionsWhenRestoreStateFromFile() {
        // Восстанавливаем состояние трекера из файла
        FileBackedTaskManager taskManagerFromFile = FileBackedTaskManager.loadFromFile(sourceFile, saveFile);

        // Создаём новую задачу с id, который формирует метод трекера, и добавляем задачу в трекер
        Task newTask = new Task(taskManagerFromFile.nextId(), "task name", "task descr", TaskStatus.NEW);
        taskManagerFromFile.addBasicTask(newTask);

        // Ожидаем, что теперь у нас две задачи в трекере (одна из файла и одна добавленная вручную)
        // При необновлении поля globalId получили бы одну (новая задача перезаписала бы старую, т.к. их id равны)
        assertEquals(2, taskManagerFromFile.getAllBasicTasks().size(),
                "Некорректное добавление задач в трекер");
    }
}