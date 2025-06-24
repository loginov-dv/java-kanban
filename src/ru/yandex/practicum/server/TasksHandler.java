package ru.yandex.practicum.server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.managers.TaskManager;
import ru.yandex.practicum.tasks.Task;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    private final TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        switch (method) {
            case "GET":
                if (pathParts.length == 2) {
                    try {
                        // GET /tasks
                        List<Task> tasks = taskManager.getAllBasicTasks();
                        String tasksJson = gson.toJson(tasks);
                        writeResponse(exchange, tasksJson, 200);
                        break;
                    } catch (Exception exception) {
                        writeResponse(exchange, "Ошибка: " + exception.getMessage(), 500);
                    }
                } else if (pathParts.length == 3) {
                    try {
                        // GET /tasks/{id}
                        Optional<Integer> maybeId = getIdFromPath(path);
                        if (maybeId.isEmpty()) {
                            writeResponse(exchange, "Некорректный id задачи", 404);
                            break;
                        } else {
                            Optional<Task> maybeTask = taskManager.getBasicTaskById(maybeId.get());

                            if (maybeTask.isEmpty()) {
                                writeResponse(exchange, "Задача с id = " + maybeId.get() + " не найдена",
                                        404);
                                break;
                            } else {
                                Task task = maybeTask.get();
                                String taskJson = gson.toJson(task);
                                writeResponse(exchange, taskJson, 200);
                                break;
                            }
                        }
                    } catch (Exception exception) {
                        writeResponse(exchange, "Ошибка: " + exception.getMessage(), 500);
                    }
                } else {
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
                    break;
                }
            case "POST":
                if (pathParts.length == 2) {
                    try {
                        // POST /tasks
                        // получаем входящий поток байтов
                        InputStream inputStream = exchange.getRequestBody();
                        // дожидаемся получения всех данных в виде массива байтов и конвертируем их в строку
                        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        JsonElement jsonElement = JsonParser.parseString(body);
                        if(!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
                            writeResponse(exchange, "Некорректный формат", 400);
                            break;
                        }
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        JsonElement idJson = jsonObject.get("id");
                        if (idJson == null) { // Передан task без id - новый
                            Task task = gson.fromJson(body, Task.class);
                            if (task.getID() == 0) {
                                task = new Task(taskManager.nextId(), task.getName(), task.getDescription(),
                                        task.getStatus(), task.getStartTime().orElse(null), task.getDuration());
                            }
                            taskManager.addBasicTask(task);
                            writeResponse(exchange, "", 201);
                        } else { // Передан task с id - модификация
                            Task task = gson.fromJson(body, Task.class);
                            taskManager.updateBasicTask(task);
                            writeResponse(exchange, "", 201);
                            break;
                        }
                    } catch (Exception exception) {
                        writeResponse(exchange, "Ошибка: " + exception.getMessage(), 500);
                    }
                } else {
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
                }
            case "DELETE":
                if (pathParts.length == 3) {
                    try {
                        // DELETE /tasks/{id}
                        Optional<Integer> maybeId = getIdFromPath(path);
                        if (maybeId.isEmpty()) {
                            // В ТЗ не сказано обрабатывать такую ситуацию
                            writeResponse(exchange, "Некорректный id задачи", 404);
                        } else {
                            taskManager.removeBasicTaskById(maybeId.get());
                            writeResponse(exchange, "", 200);
                        }
                    } catch (Exception exception) {
                        writeResponse(exchange, "Ошибка: " + exception.getMessage(), 500);
                    }
                } else {
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
                }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }
}
