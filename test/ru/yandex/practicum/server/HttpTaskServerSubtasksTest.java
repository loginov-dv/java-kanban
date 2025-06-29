package ru.yandex.practicum.server;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import ru.yandex.practicum.exceptions.TaskNotFoundException;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

// Класс для тестирования пути /subtasks
public class HttpTaskServerSubtasksTest extends BaseHttpTaskServerTest {
    // Проверяет получение всех подзадач (GET /subtasks)
    @Test
    void shouldGetAllSubtasks() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Отправляем запрос
        HttpResponse<String> response = sendRequest(PATH_SUBTASKS, METHOD_GET, "");

        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Получен некорректный код ответа");

        // Получаем и парсим тело ответа
        JsonElement jsonElement = JsonParser.parseString(response.body());

        // Проверяем, что сервер вернул json-массив
        assertTrue(jsonElement.isJsonArray(), "Сервер вернул не массив");

        // Получаем подзадачи и десериализуем в список
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<Subtask> subtasks = gson.fromJson(jsonArray, new TypeToken<List<Task>>() {
        }.getType());

        // Проверяем равенство коллекций
        assertIterableEquals(taskManager.getAllSubtasks(), subtasks, "Списки подзадач не равны");
    }

    // Проверяет получение одной подзадачи (GET /subtasks/{id})
    @Test
    void shouldGetSingleSubtask() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        for (Subtask subtask : taskManager.getAllSubtasks()) {
            // Отправляем запрос
            String uri = PATH_SUBTASKS + "/" + subtask.getID();
            HttpResponse<String> response = sendRequest(uri, METHOD_GET, "");

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

        // Отправляем запрос
        String uri = PATH_SUBTASKS + "/" + new Random().nextInt(100000, 200000);
        HttpResponse<String> response = sendRequest(uri, METHOD_GET, "");

        // Проверяем код ответа
        assertEquals(404, response.statusCode(), "Получен некорректный код ответа");
    }

    // Проверяет попытку передачи в качестве id строки некорректного формата (GET /subtasks/{id})
    @Test
    void shouldHandleInvalidId() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Отправляем запрос
        String uri = PATH_SUBTASKS + "/" + "test";
        HttpResponse<String> response = sendRequest(uri, METHOD_GET, "");

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

        // Отправляем запрос
        HttpResponse<String> response = sendRequest(PATH_SUBTASKS, METHOD_POST, jsonSubtask);

        // Проверяем код ответа
        assertEquals(201, response.statusCode(), "Некорректный код ответа");

        // Проверяем равенство полей задач
        assertEquals(1, taskManager.getAllSubtasks().size(), "Некорректное количество подзадач");
        Subtask subtaskInManager = taskManager.getAllSubtasks().getFirst();

        assertEquals(subtaskInManager.getName(), subtask.getName(), "Подзадачи не равны");
        assertEquals(subtaskInManager.getDescription(), subtask.getDescription(), "Подзадачи не равны");
        assertEquals(subtaskInManager.getStatus(), subtask.getStatus(), "Подзадачи не равны");
        assertTrue(subtaskInManager.getStartTime().isPresent(), "Подзадачи не равны");
        assertEquals(subtaskInManager.getStartTime().orElse(null).format(dtf),
                subtask.getStartTime().orElse(null).format(dtf), "Подзадачи не равны");
        assertEquals(subtaskInManager.getDuration(), subtask.getDuration(), "Подзадачи не равны");
        assertEquals(subtaskInManager.getEpicID(), subtask.getEpicID(), "Подзадачи не равны");
    }

    // Проверяет изменение подзадачи (POST /subtasks)
    @Test
    void shouldUpdateSubtask() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Выберем случайную подзадачу и изменим несколько её полей (имя, статус, дату начала)
        Random random = new Random();
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        Subtask randomSubtask = subtasks.get(random.nextInt(0, subtasks.size()));

        Subtask updatedSubtask = new Subtask(randomSubtask.getID(), "new name", randomSubtask.getDescription(),
                TaskStatus.NEW, randomSubtask.getEpicID(), LocalDateTime.now(), randomSubtask.getDuration());
        String jsonSubtask = gson.toJson(updatedSubtask);

        // Отправляем запрос
        HttpResponse<String> response = sendRequest(PATH_SUBTASKS, METHOD_POST, jsonSubtask);

        // Проверяем код ответа
        assertEquals(201, response.statusCode(), "Некорректный код ответа");

        // Проверяем равенство полей задач
        Subtask subtaskInManager = null;
        try {
            subtaskInManager = taskManager.getSubtaskById(updatedSubtask.getID());
        } catch (TaskNotFoundException exception) {
            fail("Подзадача не была добавлена в трекер");
        }

        assertEquals(updatedSubtask.getName(), subtaskInManager.getName(), "Подзадачи не равны");
        assertEquals(updatedSubtask.getDescription(), subtaskInManager.getDescription(), "Подзадачи не равны");
        assertEquals(updatedSubtask.getStatus(), subtaskInManager.getStatus(), "Подзадачи не равны");
        assertTrue(subtaskInManager.getStartTime().isPresent(), "Подзадачи не равны");
        assertEquals(updatedSubtask.getStartTime().orElse(null).format(dtf),
                subtaskInManager.getStartTime().orElse(null).format(dtf), "Подзадачи не равны");
        assertEquals(updatedSubtask.getDuration(), subtaskInManager.getDuration(), "Подзадачи не равны");
        assertEquals(updatedSubtask.getEpicID(), subtaskInManager.getEpicID(), "Подзадачи не равны");
    }

    // Проверяет удаление подзадачи (DELETE /subtasks/{id})
    @Test
    void shouldDeleteSubtask() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Удалим случайную подзадачу
        Random random = new Random();
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        int subtasksCount = subtasks.size();
        Task randomSubtask = subtasks.get(random.nextInt(0, subtasks.size()));

        // Отправляем запрос
        String uri = PATH_SUBTASKS + "/" + randomSubtask.getID();
        HttpResponse<String> response = sendRequest(uri, METHOD_DELETE, "");

        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Некорректный код ответа");

        // Проверим, что подзадача удалена из трекера
        subtasks = taskManager.getAllSubtasks();

        assertEquals(subtasksCount - 1, subtasks.size(), "Некорректное количество подзадач");
        assertFalse(subtasks.contains(randomSubtask), "Подзадача не была удалена из трекера");
    }

    // Проверяет добавление подзадачи, имеющей пересечение с другой задачей (POST /subtasks)
    @Test
    void shouldNotAddSubtaskThatHasOverlapWithSomeOtherTask() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Создаём подзадачу, которая имеет пересечение с другой задачей
        Random random = new Random();
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        Subtask randomSubtask = subtasks.get(random.nextInt(0, subtasks.size()));
        Subtask newSubtask = new Subtask(0, "test", "description", TaskStatus.NEW, 1000,
                randomSubtask.getStartTime().orElse(null), randomSubtask.getDuration());
        String jsonSubtask = gson.toJson(newSubtask);

        // Отправляем запрос
        HttpResponse<String> response = sendRequest(PATH_SUBTASKS, METHOD_POST, jsonSubtask);

        // Проверяем код ответа
        assertEquals(406, response.statusCode(), "Некорректный код ответа");
    }
}
