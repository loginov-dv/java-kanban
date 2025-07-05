package ru.yandex.practicum.server;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Random;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

// Класс для тестирования пути /epics
public class HttpTaskServerEpicsTest extends BaseHttpTaskServerTest {
    // Проверяет получение всех эпиков (GET /epics)
    @Test
    void shouldGetAllEpics() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Отправляем запрос
        HttpResponse<String> response = sendRequest(PATH_EPICS, METHOD_GET, "");

        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Получен некорректный код ответа");

        // Получаем и парсим тело ответа
        JsonElement jsonElement = JsonParser.parseString(response.body());

        // Проверяем, что сервер вернул json-массив
        assertTrue(jsonElement.isJsonArray(), "Сервер вернул не массив");

        // Получаем задачи и десериализуем в список
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<Epic> epics = gson.fromJson(jsonArray, new TypeToken<List<Task>>() {
        }.getType());

        // Проверяем равенство коллекций
        assertIterableEquals(taskManager.getAllEpics(), epics, "Списки эпиков не равны");
    }

    // Проверяет получение одного эпика (GET /epics/{id})
    @Test
    void shouldGetSingleEpic() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        for (Epic epic : taskManager.getAllEpics()) {
            // Отправляем запрос
            String uri = PATH_EPICS + "/" + epic.getID();
            HttpResponse<String> response = sendRequest(uri, METHOD_GET, "");

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
        fillTaskManagerWithTestData();

        // Отправляем запрос
        String uri = PATH_EPICS + "/" + new Random().nextInt(100000, 200000);
        HttpResponse<String> response = sendRequest(uri, METHOD_GET, "");

        // Проверяем код ответа
        assertEquals(404, response.statusCode(), "Получен некорректный код ответа");
    }

    // Проверяет попытку передачи в качестве id строки некорректного формата (GET /tasks/{id})
    @Test
    void shouldHandleInvalidId() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Отправляем запрос
        String uri = PATH_EPICS + "/" + "test";
        HttpResponse<String> response = sendRequest(uri, METHOD_GET, "");

        // Проверяем код ответа
        assertEquals(400, response.statusCode(), "Получен некорректный код ответа");
    }

    // Проверяет добавление эпика (POST /epics)
    @Test
    void shouldAddNewEpic() throws IOException, InterruptedException {
        // Создаём эпик
        // При передаче эпика он считается новым, если передаётся id <= 0
        // или отсутствует соответствующий элемент в JSON
        Epic epic = new Epic(0, "epic", "description");
        String jsonEpic = gson.toJson(epic);

        // Отправляем запрос
        HttpResponse<String> response = sendRequest(PATH_EPICS, METHOD_POST, jsonEpic);

        // Проверяем код ответа
        assertEquals(201, response.statusCode(), "Некорректный код ответа");

        // Проверяем равенство полей эпика
        assertEquals(1, taskManager.getAllEpics().size(), "Некорректное количество эпиков");
        Epic epicInManager = taskManager.getAllEpics().getFirst();

        assertEquals(epic.getName(), epicInManager.getName(), "Эпики не равны");
        assertEquals(epic.getDescription(), epicInManager.getDescription(), "Эпики не равны");
        assertEquals(epic.getStatus(), epicInManager.getStatus(), "Эпики не равны");
    }

    // Проверяет изменение эпика (POST /epics)
    @Test
    void shouldUpdateEpic() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Выберем случайный эпик и изменим несколько его полей (имя, описание)
        Random random = new Random();
        List<Epic> epics = taskManager.getAllEpics();
        Epic randomEpic = epics.get(random.nextInt(0, epics.size()));

        Epic updatedEpic = new Epic(randomEpic.getID(), "new name", "new description",
                randomEpic.getStatus(), randomEpic.getSubtaskIDs(), randomEpic.getStartTime().get(),
                randomEpic.getDuration(), randomEpic.getEndTime().get()); // гарантированно присутствует
        String jsonEpic = gson.toJson(updatedEpic);

        // Отправляем запрос
        HttpResponse<String> response = sendRequest(PATH_EPICS, METHOD_POST, jsonEpic);

        // Проверяем код ответа (в спецификации нет обновления эпика)
        assertEquals(400, response.statusCode(), "Некорректный код ответа");
    }

    // Проверяет удаление эпика (DELETE /epics/{id})
    @Test
    void shouldDeleteEpic() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Удалим случайный эпик
        Random random = new Random();
        List<Epic> epics = taskManager.getAllEpics();
        int epicsCount = epics.size();
        Epic randomEpic = epics.get(random.nextInt(0, epics.size()));
        List<Subtask> epicSubtasks = taskManager.getAllEpicSubtasks(randomEpic);

        // Отправляем запрос
        String uri = PATH_EPICS + "/" + randomEpic.getID();
        HttpResponse<String> response = sendRequest(uri, METHOD_DELETE, "");

        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Некорректный код ответа");

        // Проверим, что эпик удалён из трекера
        epics = taskManager.getAllEpics();

        assertEquals(epicsCount - 1, epics.size(), "Некорректное количество эпиков");
        assertFalse(epics.contains(randomEpic), "Эпик не был удалён из трекера");

        // Проверим, что удалены также подзадачи удалённого эпика
        boolean result = taskManager.getAllSubtasks().stream().noneMatch(epicSubtasks::contains);
        assertTrue(result, "Позадачи эпика не были удалены из трекера");
    }

    // Проверяет получение подзадач эпика (GET /epics/{id}/subtasks)
    @Test
    void shouldReturnEpicSubtasks() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Запросим подзадачи для случайного эпика
        Random random = new Random();
        List<Epic> epics = taskManager.getAllEpics();
        Epic randomEpic = epics.get(random.nextInt(0, epics.size()));
        List<Subtask> randomEpicSubtasks = taskManager.getAllEpicSubtasks(randomEpic);

        // Отправляем запрос
        String uri = PATH_EPICS + "/" + randomEpic.getID() + "/subtasks";
        HttpResponse<String> response = sendRequest(uri, METHOD_GET, "");

        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Некорректный код ответа");

        // Получаем и парсим тело ответа
        JsonElement jsonElement = JsonParser.parseString(response.body());
        // Проверяем, что сервер вернул json-массив
        assertTrue(jsonElement.isJsonArray(), "Сервер вернул не массив");

        // Получаем подзадачи и десериализуем в список
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<Subtask> subtasks = gson.fromJson(jsonArray, new TypeToken<List<Task>>() {
        }.getType());

        // Проверяем равенство коллекций
        assertIterableEquals(randomEpicSubtasks, subtasks, "Списки подзадач эпика не равны");
    }
}
