package ru.yandex.practicum.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exceptions.TaskNotFoundException;
import ru.yandex.practicum.tasks.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

// Класс для тестирования пути /history
public class HttpTaskServerHistoryTest extends BaseHttpTaskServerTest {

    // Проверяет корректность получения истории просмотра задач
    @Test
    void shouldReturnHistory() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Запрашиваем задачи для формирования истории
        taskManager.getBasicTaskById(1);
        taskManager.getBasicTaskById(2);
        taskManager.getSubtaskById(11);
        taskManager.getEpicById(10);

        // Создаём клиент и запрос
        URI url = URI.create("http://localhost:8080/history");
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
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());
        // Проверяем равенство коллекций
        assertIterableEquals(taskManager.getHistory(), tasks, "Списки задач не равны");
    }

    // Проверяет, что в историю не должен добавляться null
    @Test
    void shouldNotAddNullToHistory() throws IOException, InterruptedException {
        // Проверяем, что история не содержит задач
        assertEquals(0, taskManager.getHistory().size(), "История была не пуста");

        try {
            // Пытаемся получить задачу, которой нету в трекере
            taskManager.getBasicTaskById(999);
        } catch (TaskNotFoundException exception) {
            // Создаём клиент и запрос
            URI url = URI.create("http://localhost:8080/history");
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

            // Проверяем, что полученный массив пуст
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            assertTrue(jsonArray.isEmpty(), "Некорректное добавление задач в историю просмотра");
        }
    }

    // Проверяет, что задача удаляется из истории при удалении из трекера
    @Test
    void shouldRemoveTaskFromHistoryWhenRemovedFromManager() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Запрашиваем задачи для формирования истории
        taskManager.getBasicTaskById(1);
        taskManager.getBasicTaskById(2);
        taskManager.getSubtaskById(11);
        taskManager.getEpicById(10);

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

        // Проверим корректность удаления из истории
        // Создаём клиент и запрос
        url = URI.create("http://localhost:8080/history");
        request = requestBuilder
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        response = client.send(request, handler);

        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Получен некорректный код ответа");

        // Получаем и парсим тело ответа
        JsonElement jsonElement = JsonParser.parseString(response.body());
        // Проверяем, что сервер вернул json-массив
        assertTrue(jsonElement.isJsonArray(), "Сервер вернул не массив");

        // Получаем задачи и десериализуем в список
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());
        // Проверяем равенство коллекций
        assertIterableEquals(taskManager.getHistory(), tasks, "Списки задач не равны");
    }

    // Проверяет, что в истории хранится предыдущее состояние задачи
    @Test
    void shouldKeepOnlyRecentViewOfTaskInHistoryWhenUpdatedTask() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Запрашиваем задачи для формирования истории
        Task task1 = taskManager.getBasicTaskById(1);

        // Изменим поле description у задачи с id = 1 и обновим её
        String oldDescription = task1.getDescription();
        int id = task1.getID();
        String newDescription = "new description of task1";
        task1 = new Task(task1.getID(), task1.getName(), newDescription, task1.getStatus(),
                task1.getStartTime().orElse(null), task1.getDuration());
        String jsonTask = gson.toJson(task1);

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

        // Проверим, что в истории хранится предыдущее состояние задачи с id = 1
        // (задача после обновления не запрашивалась на просмотр)
        // Создаём клиент и запрос
        url = URI.create("http://localhost:8080/history");
        request = requestBuilder
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        response = client.send(request, handler);

        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Получен некорректный код ответа");

        // Получаем и парсим тело ответа
        JsonElement jsonElement = JsonParser.parseString(response.body());
        // Проверяем, что сервер вернул json-массив
        assertTrue(jsonElement.isJsonArray(), "Сервер вернул не массив");

        // Получаем задачи и десериализуем в список
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());
        // Проверяем равенство коллекций
        assertIterableEquals(taskManager.getHistory(), tasks, "Списки задач не равны");
        // Проверяем, что описание в истории не изменилось
        assertEquals(oldDescription,
                tasks.stream().filter(task -> task.getID() == id).map(Task::getDescription).findFirst().get(),
                "Некорректное состояние задачи с id = 1 в истории просмотра");

        // Получим задачу с id = 1
        taskManager.getBasicTaskById(1);

        // Проверим, что в истории теперь хранится новое состояние задачи с id = 1
        url = URI.create("http://localhost:8080/history");
        request = requestBuilder
                .GET()
                .uri(url)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();
        response = client.send(request, handler);

        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Получен некорректный код ответа");

        // Получаем и парсим тело ответа
        jsonElement = JsonParser.parseString(response.body());
        // Проверяем, что сервер вернул json-массив
        assertTrue(jsonElement.isJsonArray(), "Сервер вернул не массив");

        // Получаем задачи и десериализуем в список
        tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());
        // Проверяем равенство коллекций
        assertIterableEquals(taskManager.getHistory(), tasks, "Списки задач не равны");

        // Проверяем, что описание изменилось после запроса задачи
        assertEquals(newDescription,
                tasks.stream().filter(task -> task.getID() == id).map(Task::getDescription).findFirst().get(),
                "Некорректное состояние задачи с id = 1 в истории просмотра");
    }

    // Проверяет, что история не содержит дубликатов задач
    @Test
    void shouldKeepOnlyRecentViewOfTaskInHistoryWhenTaskViewedMultipleTimes() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Получим задачи по id
        taskManager.getBasicTaskById(1);
        taskManager.getBasicTaskById(2);
        // Ещё несколько раз запросим задачу с id = 1
        taskManager.getBasicTaskById(1);
        taskManager.getBasicTaskById(1);

        // Проверим, что задачи попали в историю
        // В истории должно быть всего две задачи, несмотря на то, что
        // одна из задач была запрошена несколько раз
        // Создаём клиент и запрос
        URI url = URI.create("http://localhost:8080/history");
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
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());
        // Проверяем равенство коллекций
        assertIterableEquals(taskManager.getHistory(), tasks, "Списки задач не равны");
        assertEquals(2, tasks.size(), "Некорректное добавление задач в историю просмотра");
    }

    // Проверяет, что при удалении эпика из трекера, он также удаляется и из истории вместе с его подзадачами
    @Test
    void shouldRemoveEpicFromHistoryWithItsSubtasksWhenRemovedFromManager() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Запросим все эпики и подзадачи
        // (в тестовых данных содержится два эпика, каждый из них содержит по одной подзадаче)
        for (Epic epic : taskManager.getAllEpics()) {
            taskManager.getEpicById(epic.getID());
        }
        for (Subtask subtask : taskManager.getAllSubtasks()) {
            taskManager.getSubtaskById(subtask.getID());
        }

        // Удалим один из эпиков
        Random random = new Random();
        List<Epic> allEpics = taskManager.getAllEpics();
        Epic randomEpic = allEpics.get(random.nextInt(0, allEpics.size()));
        int randomEpicId = randomEpic.getID();
        List<Subtask> randomEpicSubtasks = taskManager.getAllEpicSubtasks(randomEpic);
        taskManager.removeEpicById(randomEpicId);

        // Проверим, что эпика и его подзадач больше нет в истории
        // Создаём клиент и запрос
        URI url = URI.create("http://localhost:8080/history");
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
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());
        // Проверяем равенство коллекций
        assertIterableEquals(taskManager.getHistory(), tasks, "Списки задач не равны");
        assertFalse(tasks.contains(randomEpic), "Эпик не был удалён из истории");
        for (Subtask subtask : randomEpicSubtasks) {
            assertFalse(tasks.contains(subtask), "Поздадача не была удалена из истории");
        }
    }
}
