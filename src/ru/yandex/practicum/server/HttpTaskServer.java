package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.managers.Managers;
import ru.yandex.practicum.managers.TaskManager;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    // Порт
    private static final int PORT = 8080;
    // Экземпляр класса, реализующего TaskManager
    private final TaskManager taskManager;
    // HttpServer
    private HttpServer httpServer;

    // Конструктор класса HttpTaskServer
    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    // Точка входа
    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer(Managers.getInMemoryTaskManager());
        server.start();
    }

    // Запустить сервер
    public void start() throws IOException {
        // test data
        Task task1 = new Task(1, "Task1", "description", TaskStatus.NEW,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));
        Task task2 = new Task(2, "Task2", "description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, 1, 10, 10, 0), Duration.ofMinutes(60));
        taskManager.addBasicTask(task1);
        taskManager.addBasicTask(task2);
        //

        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.start();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту");
    }

    // Остановить сервер
    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }
}
