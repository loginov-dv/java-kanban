package ru.yandex.practicum.server;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.*;
import org.junit.jupiter.api.Test;
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
import java.util.List;
import java.util.Optional;

// Класс для тестирования пути /tasks
class HttpTaskServerTasksTest extends BaseHttpTaskServerTest {
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
        Optional<Task> maybeTask = taskManager.getBasicTaskById(task.getID());
        assertTrue(maybeTask.isPresent(), "Подзадача не была добавлена в трекер");

        Task taskInManager = maybeTask.get();

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

    // Проверяет добавление задачи, имеющей пересечение с другой задачей
    @Test
    void shouldNotAddTaskThatHasOverlapWithSomeOtherTask() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fill();

        // Создаём задачу, которая имеет пересечение с другой задачей
        Task task = new Task(0, "test", "description", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));
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
        assertEquals(406, response.statusCode(), "Некорректный код ответа");
    }
}
