package ru.yandex.practicum.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.managers.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    // Экземпляр класса, реализующего TaskManager
    private final TaskManager taskManager;

    // Конструктор класса HistoryHandler
    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (method.equals(GET)) {
            handleGetRequest(exchange, path);
        } else {
            writeResponse(exchange, "Метод не поддерживается", 405);
        }
    }

    // Обработка GET-запросов
    private void handleGetRequest(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");

        if (pathParts.length == 2) { // GET /history
            try {
                String historyJson = gson.toJson(taskManager.getHistory());

                writeResponse(exchange, historyJson, 200);
            } catch (Exception exception) {
                writeResponse(exchange, "Ошибка при получении истории просмотра задач: " +
                                exception.getMessage(), 500);
            }
        } else {
            writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }
}
