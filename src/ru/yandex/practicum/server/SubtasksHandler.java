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
import ru.yandex.practicum.tasks.Subtask;

// Обработчик пути /subtasks
public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    // Конструктор класса SubtasksHandler
    public SubtasksHandler(TaskManager taskManager) {
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

        if (pathParts.length == 2) { // GET /subtasks
            try {
                String subtasksJson = gson.toJson(taskManager.getAllSubtasks());

                writeResponse(exchange, subtasksJson, 200);
            } catch (Exception exception) {
                writeResponse(exchange, "Ошибка при получении подзадач: " + exception.getMessage(),
                        500);
            }
        } else if (pathParts.length == 3) { // GET /subtasks/{id}
            try {
                Optional<Integer> maybeId = getIdFromPath(path);
                if (maybeId.isEmpty()) {
                    writeResponse(exchange, "Некорректный id подзадачи", 400);
                    return;
                }

                Subtask subtask = taskManager.getSubtaskById(maybeId.get());
                String subtaskJson = gson.toJson(subtask);

                writeResponse(exchange, subtaskJson, 200);
            } catch (TaskNotFoundException taskNotFoundException) {
                writeResponse(exchange, taskNotFoundException.getMessage(), 404);
            } catch (Exception exception) {
                writeResponse(exchange, "Ошибка при получении подзадачи: " + exception.getMessage(),
                        500);
            }
        } else {
            writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    // Обработка POST-запросов
    private void handlePostRequest(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");

        if (pathParts.length == 2) { // POST /subtasks
            try {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                JsonElement jsonElement = JsonParser.parseString(body);

                if (!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
                    writeResponse(exchange, "Некорректный формат подзадачи", 400);
                    return;
                }

                // Парсим подзадачу
                Subtask subtask = gson.fromJson(body, Subtask.class);
                if (subtask.getID() <= 0) { // Передан subtask без id - создаём новую подзадачу в трекере
                    subtask = new Subtask(taskManager.nextId(), subtask.getName(), subtask.getDescription(),
                            subtask.getStatus(), subtask.getEpicID(),
                            subtask.getStartTime().orElse(null), subtask.getDuration());

                    taskManager.addSubtask(subtask);
                    writeResponse(exchange, "", 201);
                } else { // Передан subtask с id - обновляем существующую подзадачу
                    taskManager.updateSubtask(subtask);
                    writeResponse(exchange, "", 201);
                }
            } catch (TaskNotFoundException taskNotFoundException) {
                writeResponse(exchange, taskNotFoundException.getMessage(), 404);
            } catch (TaskOverlapException taskOverlapException) {
                writeResponse(exchange, "Ошибка при добавлении/обновлении подзадачи: "
                        + taskOverlapException.getMessage(), 406);
            } catch (DateTimeParseException dateTimeParseException) {
                writeResponse(exchange, "Некорректный формат даты. Требуемый формат - dd.MM.yyyy HH:mm:ss",
                        400);
            } catch (Exception exception) {
                writeResponse(exchange, "Ошибка при добавлении/обновлении подзадачи: "
                        + exception.getMessage(), 500);
            }
        } else {
            writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    // Обработка DELETE-запросов
    private void handleDeleteRequest(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");

        if (pathParts.length == 3) { // DELETE /subtasks/{id}
            try {
                Optional<Integer> maybeId = getIdFromPath(path);
                if (maybeId.isEmpty()) {
                    writeResponse(exchange, "Некорректный id подзадачи", 400);
                    return;
                }

                taskManager.removeSubtaskById(maybeId.get());
                writeResponse(exchange, "", 200);
            } catch (Exception exception) {
                writeResponse(exchange, "Ошибка при удалении подзадачи: " + exception.getMessage(),
                        500);
            }
        } else {
            writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }
}
