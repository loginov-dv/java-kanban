package ru.yandex.practicum.server;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.managers.InMemoryTaskManager;
import ru.yandex.practicum.managers.TaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

class HttpTaskServerTasksTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

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

    // Проверяет получение всех задач (GET /tasks)
    @Test
    void shouldGetAllTasks() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fill();

        // Создаём клиент и запрос
        URI url = URI.create("http://localhost:8080/tasks");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Получен некорректный код ответа");

        // Получаем и парсим тело ответа
        JsonElement jsonElement = JsonParser.parseString(response.body());

        // Проверяем, что сервер вернул json-массив
        assertTrue(jsonElement.isJsonArray(), "Сервер вернул не массив");

        // Получаем задачи и десериализуем в список
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<Task> tasks = gson.fromJson(jsonArray, new TaskListTypeToken().getType());

        // Проверяем равенство коллекций
        assertIterableEquals(taskManager.getAllBasicTasks(), tasks, "Списки задач не равны");
    }

    // Проверяет получение одной задачи (GET /tasks/{id})
    @Test
    void shouldGetSingleTask() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fill();

        for (Task task : taskManager.getAllBasicTasks()) {
            // Создаём клиент и запрос
            URI url = URI.create("http://localhost:8080/tasks/" + task.getID());
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
            HttpRequest request = requestBuilder
                    .GET()
                    .uri(url)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "application/json")
                    .build();
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);

            // Проверяем код ответа
            assertEquals(200, response.statusCode(), "Получен некорректный код ответа");

            // Проверяем, что сервер вернул json-объект
            JsonElement jsonElement = JsonParser.parseString(response.body());
            assertTrue(jsonElement.isJsonObject(), "Сервер вернул не объект");

            // Парсим задачу из json
            Task requestedTask = gson.fromJson(response.body(), Task.class);

            // Проверяем равенство полей задач
            assertEquals(task, requestedTask, "Задачи не равны");
            assertEquals(task.getName(), requestedTask.getName(), "Задачи не равны");
            assertEquals(task.getDescription(), requestedTask.getDescription(), "Задачи не равны");
            assertEquals(task.getStatus(), requestedTask.getStatus(), "Задачи не равны");
            assertEquals(task.getStartTime().orElse(null), requestedTask.getStartTime().orElse(null),
                    "Задачи не равны");
            assertEquals(task.getDuration(), requestedTask.getDuration(), "Задачи не равны");
            assertEquals(task.getEndTime().orElse(null), requestedTask.getEndTime().orElse(null),
                    "Задачи не равны");
        }
    }

    // Проверяет попытку получения задачи по id, которой нет в трекере (GET /tasks/{id})
    @Test
    void shouldNotGetTaskThatDoesntExist() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fill();

        // Создаём клиент и запрос
        URI url = URI.create("http://localhost:8080/tasks/" + 1021030);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        // Проверяем код ответа
        assertEquals(404, response.statusCode(), "Получен некорректный код ответа");
    }

    // Проверяет попытку передачи в качестве id строки некорректного формата (GET /tasks/{id})
    @Test
    void shouldHandleInvalidId() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fill();

        // Создаём клиент и запрос
        URI url = URI.create("http://localhost:8080/tasks/" + "test");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        // Проверяем код ответа
        assertEquals(400, response.statusCode(), "Получен некорректный код ответа");
    }

    // Проверяет добавление задачи (POST /tasks)
    @Test
    void shouldAddNewTask() throws IOException, InterruptedException {
        // Создаём задачу
        // При передаче задачи она считается новой, если передаётся id <= 0
        // или отсутствует соответствующий элемент в JSON
        Task task = new Task(0, "task", "description", TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofMinutes(10));
        String jsonTask = gson.toJson(task);

        // Создаём клиент и запрос
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        // Проверяем код ответа
        assertEquals(201, response.statusCode(), "Некорректный код ответа");

        // Проверяем равенство полей задач
        List<Task> tasks = taskManager.getAllBasicTasks();

        assertEquals(1, tasks.size(), "Некорректное количество задач");
        assertEquals(task.getName(), tasks.getFirst().getName(), "Задачи не равны");
        assertEquals(task.getDescription(), tasks.getFirst().getDescription(), "Задачи не равны");
        assertEquals(task.getStatus(), tasks.getFirst().getStatus(), "Задачи не равны");
        assertNotNull(tasks.getFirst().getStartTime().orElse(null), "Задачи не равны");
        assertEquals(task.getStartTime().orElse(null).format(dtf), // гарантированно не null
                tasks.getFirst().getStartTime().orElse(null).format(dtf),
                "Задачи не равны");
        assertEquals(task.getDuration(), tasks.getFirst().getDuration(), "Задачи не равны");
    }

    // Проверяет изменение задачи (POST /tasks)
    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fill();

        // У задачи с id = 1 изменим имя, статус и дату начала
        Task task = taskManager.getBasicTaskById(1).get(); // значение гарантированно присутствует
        task = new Task(task.getID(), "new name", task.getDescription(), TaskStatus.IN_PROGRESS,
                LocalDateTime.now(), task.getDuration());
        String jsonTask = gson.toJson(task);

        // Создаём клиент и запрос
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        // Проверяем код ответа
        assertEquals(201, response.statusCode(), "Некорректный код ответа");

        // Проверяем равенство полей задач
        Task taskInManager = taskManager.getBasicTaskById(1).get(); // значение гарантированно присутствует

        assertEquals(task.getName(), taskInManager.getName(), "Задачи не равны");
        assertEquals(task.getDescription(), taskInManager.getDescription(), "Задачи не равны");
        assertEquals(task.getStatus(), taskInManager.getStatus(), "Задачи не равны");
        assertNotNull(taskInManager.getStartTime().orElse(null), "Задачи не равны");
        assertEquals(task.getStartTime().orElse(null).format(dtf), // гарантированно не null
                taskInManager.getStartTime().orElse(null).format(dtf),
                "Задачи не равны");
        assertEquals(task.getDuration(), taskInManager.getDuration(), "Задачи не равны");
    }

    // Проверяет удаление задачи (DELETE /tasks/{id})
    @Test
    void shouldDeleteTask() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fill();

        // Удалим задачу с id = 1
        // Создаём клиент и запрос
        URI url = URI.create("http://localhost:8080/tasks/" + 1);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .DELETE()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Некорректный код ответа");

        // Проверим, что задача удалена из трекера
        List<Task> tasks = taskManager.getAllBasicTasks();

        assertEquals(2, tasks.size(), "Некорректное количество задач");
        assertFalse(tasks.stream().anyMatch(task -> task.getID() == 1), "Задача с id = 1 не была удалена");
    }

    // Предзаполнение тестовыми данными
    private void fill() {
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

class TaskListTypeToken extends TypeToken<List<Task>> {

}
