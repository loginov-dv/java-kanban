package ru.yandex.practicum.server;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

import ru.yandex.practicum.tasks.Task;

// Класс для тестирования пути /prioritized
public class HttpTaskServerPriorityTest extends BaseHttpTaskServerTest {

    // Проверяет корректность получения приоретизированного списка задач (GET /prioritized)
    @Test
    void shouldReturnPrioritizedTasks() throws IOException, InterruptedException {
        // Заполняем трекер тестовыми данными
        fillTaskManagerWithTestData();

        // Отправляем запрос
        HttpResponse<String> response = sendRequest(PATH_PRIORITIZED, METHOD_GET, "");

        // Проверяем код ответа
        assertEquals(200, response.statusCode(), "Получен некорректный код ответа");

        // Получаем и парсим тело ответа
        JsonElement jsonElement = JsonParser.parseString(response.body());
        // Проверяем, что сервер вернул json-массив
        assertTrue(jsonElement.isJsonArray(), "Сервер вернул не массив");

        // Получаем задачи и десериализуем в список
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        // Проверяем равенство коллекций
        assertIterableEquals(taskManager.getPrioritizedTasks(), tasks, "Списки задач не равны");
    }
}
