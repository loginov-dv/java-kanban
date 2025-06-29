package ru.yandex.practicum.server.adapters;

import com.google.gson.*;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Полиморфный JsonDeserializer для списка задач
public class TaskDeserializer implements JsonDeserializer<Task> {
    // Наименование поля, определяющего тип задачи
    private final String taskTypeElementName;
    // Объект класса Gson со всеми адаптерами
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(TaskStatus.class, new TaskStatusAdapter())
            .create();
    // Мапа, ключом которой является наименование типа задачи, а значением - соответствующий класс
    private final Map<String, Class<? extends Task>> taskTypeRegistry;

    // Конструктор класса TaskDeserializer
    public TaskDeserializer(String taskTypeElementName) {
        this.taskTypeElementName = taskTypeElementName;
        this.taskTypeRegistry = new HashMap<>();
    }

    // Добавляет в мапу новую запись
    public void setTaskTypeRegistry(String taskTypeName, Class<? extends Task> taskType) {
        taskTypeRegistry.put(taskTypeName, taskType);
    }

    // Десериализует задачу в зависимости от значения поля type
    @Override
    public Task deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) {
        JsonObject taskObject = jsonElement.getAsJsonObject();
        JsonElement taskTypeElement = taskObject.get(taskTypeElementName);

        // Если поле type отсутствует, считаем, что это базовый Task
        // В противном случае при десериализации обычных Task и при отсутствии у них type
        // (например, если в POST запросе от пользователя пришла задача без указания type)
        // метод упадёт с NullPointerException
        if (taskTypeElement == null) {
            return gson.fromJson(taskObject, Task.class);
        }

        // Иначе ищем зарегистрированный тип
        String taskTypeName = taskTypeElement.getAsString();
        Class<? extends Task> taskType = taskTypeRegistry.get(taskTypeName);

        if (taskType == null) {
            throw new JsonParseException("Неизвестный тип задачи: " + taskTypeName);
        }

        return gson.fromJson(taskObject, taskType);
    }
}
