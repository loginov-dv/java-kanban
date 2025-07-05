package ru.yandex.practicum.server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import ru.yandex.practicum.managers.TaskManager;
import ru.yandex.practicum.server.adapters.DurationAdapter;
import ru.yandex.practicum.server.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.server.adapters.TaskDeserializer;
import ru.yandex.practicum.server.adapters.TaskStatusAdapter;
import ru.yandex.practicum.tasks.*;

// Базовый класс для всех обработчиков
public abstract class BaseHttpHandler {
    // Используемая кодировка
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    // Константы для HTTP-методов
    protected static final String GET = "GET";
    protected static final String POST = "POST";
    protected static final String DELETE = "DELETE";
    // Константа индекса поля id в пути
    protected static final int ID_INDEX = 2;
    // Экземпляр класса Gson
    protected final Gson gson;
    // Экземпляр класса, реализующего TaskManager
    protected final TaskManager taskManager;

    // Конструктор класса BaseHttpHandler
    protected BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;

        // Конфигурируем JSON десериализатор списка задач
        TaskDeserializer deserializer = new TaskDeserializer("type");
        deserializer.setTaskTypeRegistry(TaskType.TASK.name(), Task.class);
        deserializer.setTaskTypeRegistry(TaskType.SUBTASK.name(), Subtask.class);
        deserializer.setTaskTypeRegistry(TaskType.EPIC.name(), Epic.class);

        // Создаём объект класса Gson, регистрируя все адаптеры
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(TaskStatus.class, new TaskStatusAdapter())
                .registerTypeAdapter(Task.class, deserializer)
                .create();
    }

    // Получить id задачи из пути
    protected Optional<Integer> getIdFromPath(String path) {
        String[] pathParts = path.split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[ID_INDEX]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    // Отправить ответ с указанной строкой в теле и с указанным кодом
    protected void writeResponse(HttpExchange exchange,
                                 String responseString, int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }
}