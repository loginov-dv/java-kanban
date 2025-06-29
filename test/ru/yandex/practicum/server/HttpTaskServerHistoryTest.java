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

import ru.yandex.practicum.exceptions.TaskNotFoundException;
import ru.yandex.practicum.tasks.*;

// Класс для тестирования пути /history
public class HttpTaskServerHistoryTest extends BaseHttpTaskServerTest {
    // Проверяет корректность получения истории просмотра задач (GET /history)
    @Test
    void shouldReturnHistory() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Запрашиваем задачи для формирования истории
        taskManager.getBasicTaskById(1);
        taskManager.getBasicTaskById(2);
        taskManager.getSubtaskById(11);
        taskManager.getEpicById(10);

        // Отправляем запрос
        HttpResponse<String> response = sendRequest(PATH_HISTORY, METHOD_GET, "");

        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Получен некорректный код ответа");

        // Получаем и парсим тело ответа
        JsonElement jsonElement = JsonParser.parseString(response.body());
        // Проверяем, что сервер вернул json-массив
        assertTrue(jsonElement.isJsonArray(), "Сервер вернул не массив");

        // Получаем задачи и десериализуем в список
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        // Проверяем равенство коллекций
        assertIterableEquals(taskManager.getHistory(), tasks, "Списки задач не равны");
    }

    // Проверяет, что в историю не должен добавляться null (GET /history)
    @Test
    void shouldNotAddNullToHistory() throws IOException, InterruptedException {
        // Проверяем, что история не содержит задач
        assertEquals(0, taskManager.getHistory().size(), "История была не пуста");

        try {
            // Пытаемся получить задачу, которой нету в трекере
            taskManager.getBasicTaskById(new Random().nextInt(100000, 200000));
        } catch (TaskNotFoundException exception) {
            // Отправляем запрос
            HttpResponse<String> response = sendRequest(PATH_HISTORY, METHOD_GET, "");

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

    // Проверяет, что задача удаляется из истории при удалении из трекера (GET /history)
    @Test
    void shouldRemoveTaskFromHistoryWhenRemovedFromManager() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Запрашиваем задачи для формирования истории
        taskManager.getBasicTaskById(1);
        taskManager.getBasicTaskById(2);
        taskManager.getSubtaskById(11);
        taskManager.getEpicById(10);

        // Проверим, что задача с id = 1 присутствует в истории
        // Отправляем запрос
        HttpResponse<String> response = sendRequest(PATH_HISTORY, METHOD_GET, "");
        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Получен некорректный код ответа");
        // Получаем и парсим тело ответа
        JsonElement jsonElement = JsonParser.parseString(response.body());
        // Проверяем, что сервер вернул json-массив
        assertTrue(jsonElement.isJsonArray(), "Сервер вернул не массив");
        // Получаем задачи и десериализуем в список
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        // Проверяем присутствие задачи с id = 1
        assertTrue(tasks.stream().anyMatch(item -> item.getID() == 1),
                "Некорректное добавление в историю задач");
        // Проверяем равенство коллекций
        assertIterableEquals(taskManager.getHistory(), tasks, "Списки задач не равны");

        // Удалим задачу с id = 1
        taskManager.removeBasicTaskById(1);

        // Проверим корректность удаления из истории
        // Отправляем запрос
        response = sendRequest(PATH_HISTORY, METHOD_GET, "");
        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Получен некорректный код ответа");
        // Получаем и парсим тело ответа
        jsonElement = JsonParser.parseString(response.body());
        // Проверяем, что сервер вернул json-массив
        assertTrue(jsonElement.isJsonArray(), "Сервер вернул не массив");
        // Получаем задачи и десериализуем в список
        tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        // Проверяем отсутствие задачи с id = 1
        assertTrue(tasks.stream().noneMatch(item -> item.getID() == 1),
                "Некорректное добавление в историю задач");
        // Проверяем равенство коллекций
        assertIterableEquals(taskManager.getHistory(), tasks, "Списки задач не равны");
    }

    // Проверяет, что в истории хранится предыдущее состояние задачи (GET /history)
    @Test
    void shouldKeepOnlyRecentViewOfTaskInHistoryWhenUpdatedTask() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Выберем случайную задачу и изменим несколько одно из её полей
        Random random = new Random();
        List<Task> tasks = taskManager.getAllBasicTasks();
        int randomTaskId = tasks.get(random.nextInt(0, tasks.size())).getID();
        Task randomTask = taskManager.getBasicTaskById(randomTaskId); // добавили в историю
        String oldDescription = randomTask.getDescription();

        String newDescription = "new description of randomTask";
        Task updatedTask = new Task(randomTask.getID(), randomTask.getName(), newDescription,
                randomTask.getStatus(), randomTask.getStartTime().orElse(null), randomTask.getDuration());

        taskManager.updateBasicTask(updatedTask);

        // Проверим, что в истории хранится предыдущее состояние этой задачи
        // (задача после обновления не запрашивалась на просмотр)
        // Отправляем запрос
        HttpResponse<String> response = sendRequest(PATH_HISTORY, METHOD_GET, "");
        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Получен некорректный код ответа");
        // Получаем и парсим тело ответа
        JsonElement jsonElement = JsonParser.parseString(response.body());
        // Проверяем, что сервер вернул json-массив
        assertTrue(jsonElement.isJsonArray(), "Сервер вернул не массив");
        // Получаем задачи и десериализуем в список
        tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        // Проверяем, что описание в истории не изменилось
        assertEquals(oldDescription,
                tasks.stream()
                        .filter(task -> task.getID() == randomTaskId)
                        .map(Task::getDescription)
                        .findFirst().get(),
                "Некорректное состояние задачи в истории просмотра");

        // Запросим задачу, тем самым добавив её в историю
        taskManager.getBasicTaskById(randomTaskId);

        // Проверим, что в истории теперь хранится новое состояние задачи
        response = sendRequest(PATH_HISTORY, METHOD_GET, "");
        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Получен некорректный код ответа");
        // Получаем и парсим тело ответа
        jsonElement = JsonParser.parseString(response.body());
        // Проверяем, что сервер вернул json-массив
        assertTrue(jsonElement.isJsonArray(), "Сервер вернул не массив");
        // Получаем задачи и десериализуем в список
        tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        // Проверяем, что описание изменилось после запроса задачи
        assertEquals(newDescription,
                tasks.stream()
                        .filter(task -> task.getID() == randomTaskId)
                        .map(Task::getDescription)
                        .findFirst().get(),
                "Некорректное состояние задачи в истории просмотра");
    }

    // Проверяет, что история не содержит дубликатов задач (GET /history)
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
        // Отправляем запрос
        HttpResponse<String> response = sendRequest(PATH_HISTORY, METHOD_GET, "");

        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Получен некорректный код ответа");

        // Получаем и парсим тело ответа
        JsonElement jsonElement = JsonParser.parseString(response.body());
        // Проверяем, что сервер вернул json-массив
        assertTrue(jsonElement.isJsonArray(), "Сервер вернул не массив");
        // Получаем задачи и десериализуем в список
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        // Проверяем равенство коллекций
        assertIterableEquals(taskManager.getHistory(), tasks, "Списки задач не равны");
        assertEquals(2, tasks.size(), "Некорректное добавление задач в историю просмотра");
    }

    // Проверяет, что при удалении эпика из трекера, он также удаляется и из истории вместе с его подзадачами
    // (GET /history)
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
        // Отправляем запрос
        HttpResponse<String> response = sendRequest(PATH_HISTORY, METHOD_GET, "");

        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Получен некорректный код ответа");

        // Получаем и парсим тело ответа
        JsonElement jsonElement = JsonParser.parseString(response.body());
        // Проверяем, что сервер вернул json-массив
        assertTrue(jsonElement.isJsonArray(), "Сервер вернул не массив");
        // Получаем задачи и десериализуем в список
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        // Проверяем равенство коллекций
        assertIterableEquals(taskManager.getHistory(), tasks, "Списки задач не равны");
        assertFalse(tasks.contains(randomEpic), "Эпик не был удалён из истории");
        for (Subtask subtask : randomEpicSubtasks) {
            assertFalse(tasks.contains(subtask), "Поздадача не была удалена из истории");
        }
    }
}
