package ru.yandex.practicum.server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import ru.yandex.practicum.exceptions.TaskNotFoundException;
import ru.yandex.practicum.exceptions.TaskOverlapException;
import ru.yandex.practicum.managers.TaskManager;
import ru.yandex.practicum.tasks.Task;

// Обработчик пути /tasks
public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    // Конструктор класса TasksHandler
    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (method) {
            case GET:
                handleGetRequest(exchange, path);
                break;
            case POST:
                handlePostRequest(exchange, path);
                break;
            case DELETE:
                handleDeleteRequest(exchange, path);
                break;
            default:
                writeResponse(exchange, "Метод не поддерживается", 405);
        }
    }

    // Обработка GET-запросов
    private void handleGetRequest(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");

        if (pathParts.length == 2) { // GET /tasks
            try {
                String tasksJson = gson.toJson(taskManager.getAllBasicTasks());

                writeResponse(exchange, tasksJson, 200);
            } catch (Exception exception) {
                writeResponse(exchange, "Ошибка при получении задач: " + exception.getMessage(),
                        500);
            }
        } else if (pathParts.length == 3) { // GET /tasks/{id}
            try {
                Optional<Integer> maybeId = getIdFromPath(path);
                if (maybeId.isEmpty()) {
                    writeResponse(exchange, "Некорректный id задачи", 400);
                    return;
                }

                Task task = taskManager.getBasicTaskById(maybeId.get());
                String taskJson = gson.toJson(task);

                writeResponse(exchange, taskJson, 200);
            } catch (TaskNotFoundException taskNotFoundException) {
                writeResponse(exchange, taskNotFoundException.getMessage(), 404);
            } catch (Exception exception) {
                writeResponse(exchange, "Ошибка при получении задачи: " + exception.getMessage(),
                        500);
            }
        } else {
            writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    // Обработка POST-запросов
    private void handlePostRequest(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");

        if (pathParts.length == 2) { // POST /tasks
            try {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                JsonElement jsonElement = JsonParser.parseString(body);

                if(!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
                    writeResponse(exchange, "Некорректный формат задачи", 400);
                    return;
                }

                JsonObject jsonObject = jsonElement.getAsJsonObject();
                // Парсим задачу
                Task task = gson.fromJson(body, Task.class);
                // Проверяем, был ли передан id задачи
                JsonElement idJson = jsonObject.get("id");
                if (idJson == null || task.getID() <= 0) { // Передан task без id - создаём новую задачу в трекере
                    task = new Task(taskManager.nextId(), task.getName(), task.getDescription(),
                            task.getStatus(), task.getStartTime().orElse(null), task.getDuration());
                    taskManager.addBasicTask(task);
                    writeResponse(exchange, "", 201);
                } else { // Передан task с id - обновляем существующую задачу
                    taskManager.updateBasicTask(task);
                    writeResponse(exchange, "", 201);
                }
            } catch (TaskOverlapException taskOverlapException) {
                writeResponse(exchange, "Ошибка при добавлении/обновлении задачи: "
                        + taskOverlapException.getMessage(), 406);
            } catch (DateTimeParseException dateTimeParseException) {
                writeResponse(exchange, "Некорректный формат даты. Требуемый формат - dd.MM.yyyy HH:mm:ss",
                        400);
            } catch (Exception exception) {
                writeResponse(exchange, "Ошибка при добавлении/обновлении задачи: "
                        + exception.getMessage(), 500);
            }
        } else {
            writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    // Обработка DELETE-запросов
    private void handleDeleteRequest(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");

        if (pathParts.length == 3) { // DELETE /tasks/{id}
            try {
                Optional<Integer> maybeId = getIdFromPath(path);
                if (maybeId.isEmpty()) {
                    writeResponse(exchange, "Некорректный id задачи", 400);
                    return;
                }

                taskManager.removeBasicTaskById(maybeId.get());
                writeResponse(exchange, "", 200);
            } catch (Exception exception) {
                writeResponse(exchange, "Ошибка при удалении задачи: " + exception.getMessage(),
                        500);
            }
        } else {
            writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }
}
