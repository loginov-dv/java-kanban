package ru.yandex.practicum.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.tasks.Task;

public class TasksHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final Gson gson = new GsonBuilder().create();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        switch (method) {
            case "GET":
                if (pathParts.length == 2) {
                    // GET /tasks
                    List<Task> tasks = HttpTaskServer.manager.getAllBasicTasks();
                    String tasksJson = gson.toJson(tasks);
                    writeResponse(exchange, tasksJson, 200);
                    break;
                } else if (pathParts.length == 3) {
                    // GET /tasks/{id}
                    Optional<Integer> maybeId = getIdFromPath(path);
                    if (maybeId.isEmpty()) {
                        writeResponse(exchange, "Некорректный id задачи", 404);
                    } else {
                        Optional<Task> maybeTask = HttpTaskServer.manager.getBasicTaskById(maybeId.get());

                        if (maybeTask.isEmpty()) {
                            writeResponse(exchange, "Задача с id = " + maybeId.get() + " не найдена",
                                    404);
                        } else {
                            Task task = maybeTask.get();
                            String taskJson = gson.toJson(task);
                            writeResponse(exchange, taskJson, 200);
                        }
                    }
                } else {
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
                }
            case "POST":
                if (pathParts.length == 2) {
                    // POST /tasks
                } else {
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
                }
            case "DELETE":
                if (pathParts.length == 3) {
                    // DELETE /tasks/{id}
                    Optional<Integer> maybeId = getIdFromPath(path);
                    if (maybeId.isEmpty()) {
                        // В ТЗ не сказано обрабатывать такую ситуацию
                        writeResponse(exchange, "Некорректный id задачи", 404);
                    } else {
                        HttpTaskServer.manager.removeBasicTaskById(maybeId.get());
                        writeResponse(exchange, "", 200);
                    }
                } else {
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
                }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private Optional<Integer> getIdFromPath(String path) {
        String[] pathParts = path.split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private void writeResponse(HttpExchange exchange, String responseString,
                               int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }
}
