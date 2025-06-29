package ru.yandex.practicum.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
import java.util.List;

import ru.yandex.practicum.managers.InMemoryTaskManager;
import ru.yandex.practicum.managers.TaskManager;
import ru.yandex.practicum.tasks.*;

// Базовый класс для тестов путей
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
    protected static final String GET = "GET";
    protected static final String POST = "POST";
    protected static final String DELETE = "DELETE";

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
    protected void fillTaskManagerWithTestData() {
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

    protected HttpResponse<String> sendRequest(String uriString, String method, String body)
            throws IOException, InterruptedException {
        URI uri = URI.create(uriString);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        switch (method) {
            case GET:
                requestBuilder.GET();
                break;
            case POST:
                requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body));
                break;
            case DELETE:
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

// Вспомогательный класс для десериализации списков задач
class TaskListTypeToken extends TypeToken<List<Task>> {

}

// Вспомогательный класс для десериализации списков подзадач
class SubtaskListTypeToken extends TypeToken<List<Subtask>> {

}

// Вспомогательный класс для десериализации списков эпиков
class EpicListTypeToken extends TypeToken<List<Epic>> {

}
