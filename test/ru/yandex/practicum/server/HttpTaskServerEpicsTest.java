package ru.yandex.practicum.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Класс для тестирования пути /epics
public class HttpTaskServerEpicsTest extends BaseHttpTaskServerTest {
    // Проверяет получение всех эпиков (GET /epics)
    @Test
    void shouldGetAllEpics() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fill();

        // Создаём клиент и запрос
        URI url = URI.create("http://localhost:8080/epics");
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
        List<Epic> epics = gson.fromJson(jsonArray, new EpicListTypeToken().getType());

        // Проверяем равенство коллекций
        assertIterableEquals(taskManager.getAllEpics(), epics, "Списки эпиков не равны");
    }

    // Проверяет получение одного эпика (GET /epics/{id})
    @Test
    void shouldGetSingleEpic() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fill();

        for (Epic epic : taskManager.getAllEpics()) {
            // Создаём клиент и запрос
            URI url = URI.create("http://localhost:8080/epics/" + epic.getID());
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

            // Парсим эпик из json
            Epic requestedEpic = gson.fromJson(response.body(), Epic.class);

            // Проверяем равенство полей задач
            assertEquals(epic, requestedEpic, "Эпики не равны");
            assertEquals(epic.getName(), requestedEpic.getName(), "Эпики не равны");
            assertEquals(epic.getDescription(), requestedEpic.getDescription(), "Эпики не равны");
            assertEquals(epic.getStatus(), requestedEpic.getStatus(), "Эпики не равны");
            assertEquals(epic.getStartTime().orElse(null), requestedEpic.getStartTime().orElse(null),
                    "Эпики не равны");
            assertEquals(epic.getDuration(), requestedEpic.getDuration(), "Эпики не равны");
            assertEquals(epic.getEndTime().orElse(null), requestedEpic.getEndTime().orElse(null),
                    "Эпики не равны");
            assertIterableEquals(epic.getSubtaskIDs(), requestedEpic.getSubtaskIDs(), "Эпики не равны");
        }
    }

    // Проверяет попытку получения эпика по id, которого нет в трекере (GET /epics/{id})
    @Test
    void shouldNotGetEpicThatDoesntExist() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fill();

        // Создаём клиент и запрос
        URI url = URI.create("http://localhost:8080/epics/" + 1021030);
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
        URI url = URI.create("http://localhost:8080/epics/" + "test");
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

    // Проверяет добавление эпика (POST /epics)
    @Test
    void shouldAddNewEpic() throws IOException, InterruptedException {
        // Создаём эпик
        // При передаче эпика он считается новым, если передаётся id <= 0
        // или отсутствует соответствующий элемент в JSON
        Epic epic = new Epic(0, "epic", "description", TaskStatus.NEW);
        String jsonEpic = gson.toJson(epic);

        // Создаём клиент и запрос
        URI url = URI.create("http://localhost:8080/epics/");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        // Проверяем код ответа
        assertEquals(201, response.statusCode(), "Некорректный код ответа");

        // Проверяем равенство полей эпика
        List<Epic> epics = taskManager.getAllEpics();

        assertEquals(1, epics.size(), "Некорректное количество эпиков");
        assertEquals(epic.getName(), epics.getFirst().getName(), "Эпики не равны");
        assertEquals(epic.getDescription(), epics.getFirst().getDescription(), "Эпики не равны");
        assertEquals(epic.getStatus(), epics.getFirst().getStatus(), "Эпики не равны");
    }

    // Проверяет изменение эпика (POST /epics)
    @Test
    void shouldUpdateEpic() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fill();

        // У эпика с id = 10 изменим имя
        Epic epic = taskManager.getEpicById(10);
        epic = new Epic(epic.getID(), "new name", epic.getDescription(), epic.getStatus(), epic.getSubtaskIDs(),
                epic.getStartTime().get(), epic.getDuration(), epic.getEndTime().get()); // гарантированно присутствует
        String jsonEpic = gson.toJson(epic);

        // Создаём клиент и запрос
        URI url = URI.create("http://localhost:8080/epics/");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        // Проверяем код ответа - в спецификации нет обновления эпика
        assertEquals(400, response.statusCode(), "Некорректный код ответа");
    }

    // Проверяет удаление эпика (DELETE /epics/{id})
    @Test
    void shouldDeleteEpic() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fill();

        // Удалим эпик с id = 10
        // Создаём клиент и запрос
        URI url = URI.create("http://localhost:8080/epics/" + 10);
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

        // Проверим, что эпик удалён из трекера
        List<Epic> epics = taskManager.getAllEpics();

        assertEquals(1, epics.size(), "Некорректное количество эпиков");
        assertFalse(epics.stream().anyMatch(epic -> epic.getID() == 10), "Эпик с id = 10 не был удалён");
    }

    // Проверяет получение подзадач эпика (GET /epics/{id}/subtasks)
    @Test
    void shouldReturnEpicSubtasks() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fill();

        // Запросим подзадачи эпика с id = 10
        Epic epic = taskManager.getEpicById(10);
        // Создаём клиент и запрос
        URI url = URI.create("http://localhost:8080/epics/" + epic.getID() + "/subtasks");
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
        assertEquals(200, response.statusCode(), "Некорректный код ответа");

        // Получаем и парсим тело ответа
        JsonElement jsonElement = JsonParser.parseString(response.body());
        // Проверяем, что сервер вернул json-массив
        assertTrue(jsonElement.isJsonArray(), "Сервер вернул не массив");

        // Получаем подзадачи и десериализуем в список
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<Subtask> subtasks = gson.fromJson(jsonArray, new SubtaskListTypeToken().getType());

        // Проверяем равенство коллекций
        assertIterableEquals(taskManager.getAllEpicSubtasks(epic), subtasks, "Списки подзадач эпика не равны");
    }
}
