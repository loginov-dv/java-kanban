package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import ru.yandex.practicum.managers.Managers;
import ru.yandex.practicum.managers.TaskManager;

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
        // Создаём сервер
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        // Регистрируем обработчики
        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PriorityHandler(taskManager));
        httpServer.start();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту");
    }

    // Остановить сервер
    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
            System.out.println("HTTP-сервер завершил работу");
        }
    }
}
