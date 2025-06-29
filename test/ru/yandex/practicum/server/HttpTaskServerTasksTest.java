package ru.yandex.practicum.server;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import ru.yandex.practicum.exceptions.TaskNotFoundException;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

// Класс для тестирования пути /tasks
class HttpTaskServerTasksTest extends BaseHttpTaskServerTest {
    // Проверяет получение всех задач (GET /tasks)
    @Test
    void shouldGetAllTasks() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Отправляем запрос
        HttpResponse<String> response = sendRequest(PATH_TASKS, METHOD_GET, "");

        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Получен некорректный код ответа");

        // Получаем и парсим тело ответа
        JsonElement jsonElement = JsonParser.parseString(response.body());

        // Проверяем, что сервер вернул json-массив
        assertTrue(jsonElement.isJsonArray(), "Сервер вернул не массив");

        // Получаем задачи и десериализуем в список
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<Task> tasks = gson.fromJson(jsonArray, new TypeToken<List<Task>>() {
        }.getType());

        // Проверяем равенство коллекций
        assertIterableEquals(taskManager.getAllBasicTasks(), tasks, "Списки задач не равны");
    }

    // Проверяет получение одной задачи (GET /tasks/{id})
    @Test
    void shouldGetSingleTask() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        for (Task task : taskManager.getAllBasicTasks()) {
            // Отправляем запрос
            String uri = PATH_TASKS + "/" + task.getID();
            HttpResponse<String> response = sendRequest(uri, METHOD_GET, "");

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
        fillTaskManagerWithTestData();

        // Отправляем запрос
        String uri = PATH_TASKS + "/" + new Random().nextInt(100000, 200000);
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
        String uri = PATH_TASKS + "/" + "test";
        HttpResponse<String> response = sendRequest(uri, METHOD_GET, "");

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

        // Отправляем запрос
        HttpResponse<String> response = sendRequest(PATH_TASKS, METHOD_POST, jsonTask);

        // Проверяем код ответа
        assertEquals(201, response.statusCode(), "Некорректный код ответа");

        // Проверяем равенство полей задач
        assertEquals(1, taskManager.getAllBasicTasks().size(), "Некорректное количество задач");
        Task taskInManager = taskManager.getAllBasicTasks().getFirst();

        assertEquals(taskInManager.getName(), task.getName(), "Задачи не равны");
        assertEquals(taskInManager.getDescription(), task.getDescription(), "Задачи не равны");
        assertEquals(taskInManager.getStatus(), task.getStatus(), "Задачи не равны");
        assertTrue(task.getStartTime().isPresent(), "Задачи не равны");
        assertEquals(taskInManager.getStartTime().orElse(null).format(dtf),
                task.getStartTime().orElse(null).format(dtf), "Задачи не равны");
        assertEquals(taskInManager.getDuration(), task.getDuration(), "Задачи не равны");
    }

    // Проверяет изменение задачи (POST /tasks)
    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Выберем случайную задачу и изменим несколько её полей (имя, статус, дату начала)
        Random random = new Random();
        List<Task> tasks = taskManager.getAllBasicTasks();
        Task randomTask = tasks.get(random.nextInt(0, tasks.size()));

        Task updatedTask = new Task(randomTask.getID(), "new name", randomTask.getDescription(),
                TaskStatus.IN_PROGRESS, LocalDateTime.now(), randomTask.getDuration());
        String jsonTask = gson.toJson(updatedTask);

        // Отправляем запрос
        HttpResponse<String> response = sendRequest(PATH_TASKS, METHOD_POST, jsonTask);

        // Проверяем код ответа
        assertEquals(201, response.statusCode(), "Некорректный код ответа");

        // Проверяем равенство полей задач
        Task taskInManager = null;
        try {
            taskInManager = taskManager.getBasicTaskById(updatedTask.getID());
        } catch (TaskNotFoundException exception) {
            fail("Задача не была добавлена в трекер");
        }

        assertEquals(updatedTask.getName(), taskInManager.getName(), "Задачи не равны");
        assertEquals(updatedTask.getDescription(), taskInManager.getDescription(), "Задачи не равны");
        assertEquals(updatedTask.getStatus(), taskInManager.getStatus(), "Задачи не равны");
        assertTrue(updatedTask.getStartTime().isPresent(), "Задачи не равны");
        assertEquals(updatedTask.getStartTime().orElse(null).format(dtf),
                taskInManager.getStartTime().orElse(null).format(dtf), "Задачи не равны");
        assertEquals(updatedTask.getDuration(), taskInManager.getDuration(), "Задачи не равны");
    }

    // Проверяет удаление задачи (DELETE /tasks/{id})
    @Test
    void shouldDeleteTask() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Удалим случайную задачу
        Random random = new Random();
        List<Task> tasks = taskManager.getAllBasicTasks();
        int tasksCount = tasks.size();
        Task randomTask = tasks.get(random.nextInt(0, tasks.size()));

        // Отправляем запрос
        String uri = PATH_TASKS + "/" + randomTask.getID();
        HttpResponse<String> response = sendRequest(uri, METHOD_DELETE, "");

        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Некорректный код ответа");

        // Проверим, что задача удалена из трекера
        tasks = taskManager.getAllBasicTasks();

        assertEquals(tasksCount - 1, tasks.size(), "Некорректное количество задач");
        assertFalse(tasks.contains(randomTask), "Задача не была удалена из трекера");
    }

    // Проверяет добавление задачи, имеющей пересечение с другой задачей (POST /tasks)
    @Test
    void shouldNotAddTaskThatHasOverlapWithSomeOtherTask() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Создаём задачу, которая имеет пересечение с другой задачей
        Random random = new Random();
        List<Task> tasks = taskManager.getAllBasicTasks();
        Task randomTask = tasks.get(random.nextInt(0, tasks.size()));
        Task newTask = new Task(0, "test", "description", TaskStatus.NEW,
                randomTask.getStartTime().orElse(null), randomTask.getDuration());
        String jsonTask = gson.toJson(newTask);

        // Отправляем запрос
        HttpResponse<String> response = sendRequest(PATH_TASKS, METHOD_POST, jsonTask);

        // Проверяем код ответа
        assertEquals(406, response.statusCode(), "Некорректный код ответа");
    }
}
