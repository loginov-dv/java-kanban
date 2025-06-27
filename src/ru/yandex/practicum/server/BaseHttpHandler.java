package ru.yandex.practicum.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import ru.yandex.practicum.managers.TaskManager;
import ru.yandex.practicum.tasks.TaskStatus;

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
    protected final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(TaskStatus.class, new TaskStatusAdapter())
            .create();
    // Экземпляр класса, реализующего TaskManager
    protected final TaskManager taskManager;

    // Конструктор класса BaseHttpHandler
    protected BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
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

// TypeAdapter для преобразования Duration в long
class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        if (duration == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(duration.toMinutes());
        }
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        return Duration.ofMinutes(jsonReader.nextLong());
    }
}

// TypeAdapter для преобразования LocalDateTime в String
class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    // Форматтер для LocalDateTime
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(localDateTime.format(dtf));
        }
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString(), dtf);
    }
}

// TypeAdapter для преобразования TaskStatus в String
class TaskStatusAdapter extends TypeAdapter<TaskStatus> {
    @Override
    public void write(final JsonWriter jsonWriter, final TaskStatus taskStatus) throws IOException {
        if (taskStatus == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(taskStatus.name());
        }
    }

    @Override
    public TaskStatus read(final JsonReader jsonReader) throws IOException {
        try {
            return TaskStatus.valueOf(jsonReader.nextString());
        } catch (IllegalArgumentException e) {
            // Возвращаем NEW в качестве значения по умолчанию, если была передана некорректная строка
            // (в противном случае поле будет устанавливаться в null)
            return TaskStatus.NEW;
        }
    }
}
