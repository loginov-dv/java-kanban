package ru.yandex.practicum.server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import ru.yandex.practicum.exceptions.TaskNotFoundException;
import ru.yandex.practicum.exceptions.TaskOverlapException;
import ru.yandex.practicum.managers.TaskManager;
import ru.yandex.practicum.tasks.Epic;

// Обработчик пути /epics
public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    // Конструктор класса EpicsHandler
    public EpicsHandler(TaskManager taskManager) {
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

        if (pathParts.length == 2) { // GET /epics
            try {
                String epicsJson = gson.toJson(taskManager.getAllEpics());

                writeResponse(exchange, epicsJson, 200);
            } catch (Exception exception) {
                writeResponse(exchange, "Ошибка при получении эпиков: " + exception.getMessage(),
                        500);
            }
        } else if (pathParts.length == 3) { // GET /epics/{id}
            try {
                Optional<Integer> maybeId = getIdFromPath(path);
                if (maybeId.isEmpty()) {
                    writeResponse(exchange, "Некорректный id эпика", 400);
                    return;
                }

                Epic epic = taskManager.getEpicById(maybeId.get());
                String epicJson = gson.toJson(epic);

                writeResponse(exchange, epicJson, 200);
            } catch (TaskNotFoundException taskNotFoundException) {
                writeResponse(exchange, taskNotFoundException.getMessage(), 404);
            } catch (Exception exception) {
                writeResponse(exchange, "Ошибка при получении эпика: " + exception.getMessage(),
                        500);
            }
        } else if (pathParts.length == 4) { // GET /epics/{id}/subtasks
            try {
                Optional<Integer> maybeId = getIdFromPath(path);
                if (maybeId.isEmpty()) {
                    writeResponse(exchange, "Некорректный id эпика", 400);
                    return;
                }

                if (!path.endsWith("subtasks")) {
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
                    return;
                }

                Epic epic = taskManager.getEpicById(maybeId.get());
                String epicSubtasksJson = gson.toJson(taskManager.getAllEpicSubtasks(epic));

                writeResponse(exchange, epicSubtasksJson, 200);
            } catch (TaskNotFoundException taskNotFoundException) {
                writeResponse(exchange, taskNotFoundException.getMessage(), 404);
            } catch (Exception exception) {
                writeResponse(exchange, "Ошибка при получении эпика: " + exception.getMessage(),
                        500);
            }
        } else {
            writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    // Обработка POST-запросов
    private void handlePostRequest(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");

        if (pathParts.length == 2) { // POST /epics
            try {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                JsonElement jsonElement = JsonParser.parseString(body);

                if (!jsonElement.isJsonObject()) { // проверяем, точно ли мы получили JSON-объект
                    writeResponse(exchange, "Некорректный формат эпика", 400);
                    return;
                }

                // Парсим эпик
                Epic epic = gson.fromJson(body, Epic.class);
                if (epic.getID() <= 0) { // Передан epic без id - создаём новый эпик в трекере
                    epic = new Epic(taskManager.nextId(), epic.getName(), epic.getDescription());

                    taskManager.addEpic(epic);
                    writeResponse(exchange, "", 201);
                } else { // Передан epic с id - по спецификации нет опции обновления эпика
                    writeResponse(exchange, "Обновление эпика не поддерживается", 400);
                }
            } catch (TaskNotFoundException taskNotFoundException) {
                writeResponse(exchange, taskNotFoundException.getMessage(), 404);
            } catch (Exception exception) {
                writeResponse(exchange, "Ошибка при добавлении/обновлении эпика: "
                        + exception.getMessage(), 500);
            }
        } else {
            writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    // Обработка DELETE-запросов
    private void handleDeleteRequest(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");

        if (pathParts.length == 3) { // DELETE /epics/{id}
            try {
                Optional<Integer> maybeId = getIdFromPath(path);
                if (maybeId.isEmpty()) {
                    writeResponse(exchange, "Некорректный id эпика", 400);
                    return;
                }

                taskManager.removeEpicById(maybeId.get());
                writeResponse(exchange, "", 200);
            } catch (Exception exception) {
                writeResponse(exchange, "Ошибка при удалении эпика: " + exception.getMessage(),
                        500);
            }
        } else {
            writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }
}
