package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

import ru.yandex.practicum.managers.Managers;
import ru.yandex.practicum.managers.TaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatus;

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
        Task task3 = new Task(3, "Task3", "description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, 2, 10, 10, 0), Duration.ofMinutes(60));
        taskManager.addBasicTask(task1);
        taskManager.addBasicTask(task2);
        taskManager.addBasicTask(task3);

        Epic epic1 = new Epic(10, "Эпик 1", "Описание", TaskStatus.NEW);
        Epic epic2 = new Epic(20, "Эпик 2", "Описание", TaskStatus.NEW);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Subtask subtask11 = new Subtask(11, "Подзадача 11", "Описание",
                TaskStatus.IN_PROGRESS, epic1.getID(),
                LocalDateTime.of(2028, 1, 1, 10, 0), Duration.ofMinutes(60));
        Subtask subtask21 = new Subtask(21, "Подзадача 21", "Описание",
                TaskStatus.DONE, epic2.getID(),
                LocalDateTime.of(2028, 1, 10, 10, 0), Duration.ofMinutes(60));
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask21);
        //

        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
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
        }
    }
}
