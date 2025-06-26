package ru.yandex.practicum.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.managers.InMemoryTaskManager;
import ru.yandex.practicum.managers.TaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Базовый класс для тестов путей
public class BaseHttpTaskServerTest {
    // Трекер задач
    protected TaskManager taskManager = new InMemoryTaskManager();
    // Сервер
    protected HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    // Форматтер для даты
    protected DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    // Экземпляр класса Gson
    protected final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(TaskStatus.class, new TaskStatusAdapter())
            .create();

    @BeforeEach
    void beforeEach() throws IOException {
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();
        taskManager.removeAllBasicTasks();
        taskServer.start();
    }

    @AfterEach
    void afterEach() {
        taskServer.stop();
    }

    // Предзаполнение тестовыми данными
    protected void fill() {
        Task task1 = new Task(1, "Task 1", "description", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));
        Task task2 = new Task(2, "Task 2", "description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, 1, 10, 10, 0), Duration.ofMinutes(60));
        Task task3 = new Task(3, "Task 3", "description", TaskStatus.DONE,
                LocalDateTime.of(2025, 2, 10, 10, 0), Duration.ofMinutes(60));
        taskManager.addBasicTask(task1);
        taskManager.addBasicTask(task2);
        taskManager.addBasicTask(task3);

        Epic epic1 = new Epic(10, "Эпик 1", "Описание", TaskStatus.NEW);
        Epic epic2 = new Epic(20, "Эпик 2", "Описание", TaskStatus.NEW);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Subtask subtask11 = new Subtask(11, "Подзадача 11", "Описание",
                TaskStatus.IN_PROGRESS, epic1.getID(),
                LocalDateTime.of(2028, 1, 1, 10, 0), Duration.ofMinutes(60));
        Subtask subtask21 = new Subtask(21, "Подзадача 21", "Описание",
                TaskStatus.DONE, epic2.getID(),
                LocalDateTime.of(2028, 1, 10, 10, 0), Duration.ofMinutes(60));
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask21);
    }
}

// Вспомогательный класс для десериализации списков задач
class TaskListTypeToken extends TypeToken<List<Task>> {

}

// Вспомогательный класс для десериализации списков подзадач
class SubtaskListTypeToken extends TypeToken<List<Subtask>> {

}
