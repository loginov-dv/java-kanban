package ru.yandex.practicum.server.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.tasks.TaskStatus;

import java.io.IOException;

// TypeAdapter для преобразования TaskStatus в String
public class TaskStatusAdapter extends TypeAdapter<TaskStatus> {
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
