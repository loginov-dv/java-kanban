package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

import ru.yandex.practicum.managers.TaskManager;

// Обработчик пути /prioritized
public class PriorityHandler extends BaseHttpHandler implements HttpHandler {
    // Конструктор класса PriorityHandler
    public PriorityHandler(TaskManager taskManager) {
        super(taskManager);
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

        if (pathParts.length == 2) { // GET /prioritized
            try {
                String priorityJson = gson.toJson(taskManager.getPrioritizedTasks());

                writeResponse(exchange, priorityJson, 200);
            } catch (Exception exception) {
                writeResponse(exchange, "Ошибка при получении приоритезированного списка задач: " +
                        exception.getMessage(), 500);
            }
        } else {
            writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }
}
