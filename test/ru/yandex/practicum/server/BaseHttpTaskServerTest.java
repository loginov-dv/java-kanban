package ru.yandex.practicum.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import ru.yandex.practicum.managers.InMemoryTaskManager;
import ru.yandex.practicum.managers.TaskManager;
import ru.yandex.practicum.server.adapters.DurationAdapter;
import ru.yandex.practicum.server.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.server.adapters.TaskDeserializer;
import ru.yandex.practicum.server.adapters.TaskStatusAdapter;
import ru.yandex.practicum.tasks.*;

// Базовый класс для тестов путей HttpTaskServer
public class BaseHttpTaskServerTest {
    // Трекер задач
    protected TaskManager taskManager = new InMemoryTaskManager();
    // Сервер
    protected HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    // Форматтер для даты
    protected DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    // Экземпляр класса Gson
    protected Gson gson;
    // Константы для HTTP-методов
    protected static final String METHOD_GET = "GET";
    protected static final String METHOD_POST = "POST";
    protected static final String METHOD_DELETE = "DELETE";
    // Константы для путей
    protected static final String PATH_TASKS = "http://localhost:8080/tasks";
    protected static final String PATH_SUBTASKS = "http://localhost:8080/subtasks";
    protected static final String PATH_EPICS = "http://localhost:8080/epics";
    protected static final String PATH_HISTORY = "http://localhost:8080/history";
    protected static final String PATH_PRIORITIZED = "http://localhost:8080/prioritized";

    // Конструктор класса BaseHttpTaskServerTest
    public BaseHttpTaskServerTest() {
        // Конфигурируем JSON десериализатор списка задач
        TaskDeserializer deserializer = new TaskDeserializer("type");
        deserializer.setTaskTypeRegistry(TaskType.TASK.name(), Task.class);
        deserializer.setTaskTypeRegistry(TaskType.SUBTASK.name(), Subtask.class);
        deserializer.setTaskTypeRegistry(TaskType.EPIC.name(), Epic.class);

        // Создаём объект класса Gson, регистрируя все адаптеры
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(TaskStatus.class, new TaskStatusAdapter())
                .registerTypeAdapter(Task.class, deserializer)
                .create();
    }

    // Очистить трекер задач и запустить сервер
    @BeforeEach
    void beforeEach() throws IOException {
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();
        taskManager.removeAllBasicTasks();
        taskServer.start();
    }

    // Остановить сервер
    @AfterEach
    void afterEach() {
        taskServer.stop();
    }

    // Наполнить менеджер тестовыми данными
    protected void fillTaskManagerWithTestData() {
        // Задачи
        Task task1 = new Task(1, "Task 1", "description", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));
        Task task2 = new Task(2, "Task 2", "description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, 1, 10, 10, 0), Duration.ofMinutes(60));
        Task task3 = new Task(3, "Task 3", "description", TaskStatus.DONE,
                LocalDateTime.of(2025, 2, 10, 10, 0), Duration.ofMinutes(60));
        taskManager.addBasicTask(task1);
        taskManager.addBasicTask(task2);
        taskManager.addBasicTask(task3);

        // Эпики
        Epic epic1 = new Epic(10, "Эпик 1", "Описание");
        Epic epic2 = new Epic(20, "Эпик 2", "Описание");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        // Подзадачи
        Subtask subtask11 = new Subtask(11, "Подзадача 11", "Описание",
                TaskStatus.IN_PROGRESS, epic1.getID(),
                LocalDateTime.of(2028, 1, 1, 10, 0), Duration.ofMinutes(60));
        Subtask subtask21 = new Subtask(21, "Подзадача 21", "Описание",
                TaskStatus.DONE, epic2.getID(),
                LocalDateTime.of(2028, 1, 10, 10, 0), Duration.ofMinutes(60));
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask21);
    }

    // Отправить HTTP-запрос с заданными параметрами
    protected HttpResponse<String> sendRequest(String uriString, String method, String body)
            throws IOException, InterruptedException {
        if (!body.isEmpty() && !method.equals(METHOD_POST)) {
            Assertions.fail("Передано тело запроса, хотя метод не является POST");
        }

        URI uri = URI.create(uriString);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        switch (method) {
            case METHOD_GET:
                requestBuilder.GET();
                break;
            case METHOD_POST:
                requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body));
                break;
            case METHOD_DELETE:
                requestBuilder.DELETE();
                break;
            default:
                Assertions.fail("Передан некорректный HTTP-метод");
        }
        HttpRequest request = requestBuilder
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        return client.send(request, handler);
    }
}
