package ru.yandex.practicum.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exceptions.TaskNotFoundException;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Класс для тестирования пути /subtasks
public class HttpTaskServerSubtasksTest extends BaseHttpTaskServerTest {
    // Проверяет получение всех подзадач (GET /subtasks)
    @Test
    void shouldGetAllSubtasks() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Создаём клиент и запрос
        URI url = URI.create("http://localhost:8080/subtasks");
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

        // Получаем подзадачи и десериализуем в список
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<Subtask> subtasks = gson.fromJson(jsonArray, new SubtaskListTypeToken().getType());

        // Проверяем равенство коллекций
        assertIterableEquals(taskManager.getAllSubtasks(), subtasks, "Списки подзадач не равны");
    }

    // Проверяет получение одной подзадачи (GET /subtasks/{id})
    @Test
    void shouldGetSingleSubtask() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        for (Subtask subtask : taskManager.getAllSubtasks()) {
            // Создаём клиент и запрос
            URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getID());
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

            // Парсим подзадачу из json
            Subtask requestedSubtask = gson.fromJson(response.body(), Subtask.class);

            // Проверяем равенство полей подзадач
            assertEquals(subtask, requestedSubtask, "Подзадачи не равны");
            assertEquals(subtask.getName(), requestedSubtask.getName(), "Подзадачи не равны");
            assertEquals(subtask.getDescription(), requestedSubtask.getDescription(), "Подзадачи не равны");
            assertEquals(subtask.getStatus(), requestedSubtask.getStatus(), "Подзадачи не равны");
            assertEquals(subtask.getStartTime().orElse(null), requestedSubtask.getStartTime().orElse(null),
                    "Подзадачи не равны");
            assertEquals(subtask.getDuration(), requestedSubtask.getDuration(), "Подзадачи не равны");
            assertEquals(subtask.getEpicID(), requestedSubtask.getEpicID(), "Подзадачи не равны");
        }
    }

    // Проверяет попытку получения подзадачи по id, которой нет в трекере (GET /subtasks/{id})
    @Test
    void shouldNotGetSubtaskThatDoesntExist() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Создаём клиент и запрос
        URI url = URI.create("http://localhost:8080/subtasks/" + 1021030);
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

    // Проверяет попытку передачи в качестве id строки некорректного формата (GET /subtasks/{id})
    @Test
    void shouldHandleInvalidId() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Создаём клиент и запрос
        URI url = URI.create("http://localhost:8080/subtasks/" + "test");
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

    // Проверяет добавление подзадачи (POST /subtasks)
    @Test
    void shouldAddNewSubtask() throws IOException, InterruptedException {
        // Создаём подзадачу
        // При передаче подзадачи она считается новой, если передаётся id <= 0
        // или отсутствует соответствующий элемент в JSON
        Subtask subtask = new Subtask(0, "subtask", "description", TaskStatus.NEW,
                100, LocalDateTime.now(), Duration.ofMinutes(10));
        String jsonSubtask = gson.toJson(subtask);

        // Создаём клиент и запрос
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        // Проверяем код ответа
        assertEquals(201, response.statusCode(), "Некорректный код ответа");

        // Проверяем равенство полей задач
        List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertEquals(1, subtasks.size(), "Некорректное количество подзадач");

        assertEquals(subtask.getName(), subtasks.getFirst().getName(), "Подзадачи не равны");
        assertEquals(subtask.getDescription(), subtasks.getFirst().getDescription(), "Подзадачи не равны");
        assertEquals(subtask.getStatus(), subtasks.getFirst().getStatus(), "Подзадачи не равны");
        assertNotNull(subtasks.getFirst().getStartTime().orElse(null), "Подзадачи не равны");
        assertEquals(subtask.getStartTime().orElse(null).format(dtf), // гарантированно не null
                subtasks.getFirst().getStartTime().orElse(null).format(dtf), "Подзадачи не равны");
        assertEquals(subtask.getDuration(), subtasks.getFirst().getDuration(), "Подзадачи не равны");
        assertEquals(subtask.getEpicID(), subtasks.getFirst().getEpicID(), "Подзадачи не равны");
    }

    // Проверяет изменение подзадачи (POST /subtasks)
    @Test
    void shouldUpdateSubtask() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // У подзадачи с id = 11 изменим имя, статус и дату начала
        Subtask subtask = taskManager.getSubtaskById(11);
        subtask = new Subtask(subtask.getID(), "new name", subtask.getDescription(), TaskStatus.NEW,
                subtask.getEpicID(), LocalDateTime.now(), subtask.getDuration());
        String jsonSubtask = gson.toJson(subtask);

        // Создаём клиент и запрос
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        // Проверяем код ответа
        assertEquals(201, response.statusCode(), "Некорректный код ответа");

        // Проверяем равенство полей задач
        Subtask subtaskFromManager = null;
        try {
            subtaskFromManager = taskManager.getSubtaskById(subtask.getID());
        } catch (TaskNotFoundException exception) {
            fail("Подзадача не была добавлена в трекер");
        }

        assertEquals(subtask.getName(), subtaskFromManager.getName(), "Подзадачи не равны");
        assertEquals(subtask.getDescription(), subtaskFromManager.getDescription(), "Подзадачи не равны");
        assertEquals(subtask.getStatus(), subtaskFromManager.getStatus(), "Подзадачи не равны");
        assertNotNull(subtaskFromManager.getStartTime().orElse(null), "Подзадачи не равны");
        assertEquals(subtask.getStartTime().orElse(null).format(dtf), // гарантированно не null
                subtaskFromManager.getStartTime().orElse(null).format(dtf), "Подзадачи не равны");
        assertEquals(subtask.getDuration(), subtaskFromManager.getDuration(), "Подзадачи не равны");
        assertEquals(subtask.getEpicID(), subtaskFromManager.getEpicID(), "Подзадачи не равны");
    }

    // Проверяет удаление подзадачи (DELETE /subtasks/{id})
    @Test
    void shouldDeleteSubtask() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Удалим подзадачу с id = 11
        // Создаём клиент и запрос
        URI url = URI.create("http://localhost:8080/subtasks/" + 11);
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

        // Проверим, что подзадача удалена из трекера
        List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertEquals(1, subtasks.size(), "Некорректное количество подзадач");
        assertFalse(subtasks.stream().anyMatch(subtask -> subtask.getID() == 11),
                "Подзадача с id = 11 не была удалена");
    }

    // Проверяет добавление подзадачи, имеющей пересечение с другой задачей (POST /subtasks)
    @Test
    void shouldNotAddSubtaskThatHasOverlapWithSomeOtherTask() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Создаём подзадачу, которая имеет пересечение с другой задачей
        Subtask subtask = new Subtask(0, "test", "description", TaskStatus.NEW, 1000,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));
        String jsonSubtask = gson.toJson(subtask);

        // Создаём клиент и запрос
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask))
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
