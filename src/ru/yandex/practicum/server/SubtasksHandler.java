package ru.yandex.practicum.server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.exceptions.TaskOverlapException;
import ru.yandex.practicum.managers.TaskManager;
import ru.yandex.practicum.tasks.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    // Экземпляр класса, реализующего TaskManager
    private final TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
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

                Optional<Subtask> maybeTask = taskManager.getSubtaskById(maybeId.get());
                if (maybeTask.isEmpty()) {
                    writeResponse(exchange, "Подзадача с id = " + maybeId.get() + " не найдена",
                            404);
                    return;
                }

                Subtask subtask = maybeTask.get();
                String subtaskJson = gson.toJson(subtask);

                writeResponse(exchange, subtaskJson, 200);
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

                if(!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
                    writeResponse(exchange, "Некорректный формат подзадачи", 400);
                    return;
                }

                JsonObject jsonObject = jsonElement.getAsJsonObject();
                // Парсим подзадачу
                Subtask subtask = gson.fromJson(body, Subtask.class);
                // Проверяем, был ли передан id задачи
                JsonElement idJson = jsonObject.get("id");
                if (idJson == null) { // Передан subtask без id - создаём новую подзадачу в трекере
                    subtask = new Subtask(taskManager.nextId(), subtask.getName(), subtask.getDescription(),
                            subtask.getStatus(), subtask.getEpicID(),
                            subtask.getStartTime().orElse(null), subtask.getDuration());
                    taskManager.addSubtask(subtask);
                    writeResponse(exchange, "", 201);
                } else { // Передан subtask с id - обновляем существующую подзадачу
                    taskManager.updateSubtask(subtask);
                    writeResponse(exchange, "", 201);
                }
            } catch (TaskOverlapException taskOverlapException) {
                writeResponse(exchange, "Ошибка при добавлении/обновлении подзадачи: "
                        + taskOverlapException.getMessage(), 406);
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
