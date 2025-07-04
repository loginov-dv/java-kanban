import ru.yandex.practicum.managers.*;
import ru.yandex.practicum.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        System.out.println("=> Создайте две задачи, эпик с тремя подзадачами и эпик без подзадач:\n");

        Task task1 = new Task(1, "Task1", "description", TaskStatus.NEW,
                LocalDateTime.of(2025, 5, 1, 10, 0), Duration.ofMinutes(60));
        Task task2 = new Task(2, "Task2", "description", TaskStatus.NEW,
                LocalDateTime.of(2025, 5, 10, 10, 0), Duration.ofMinutes(60));
        inMemoryTaskManager.addBasicTask(task1);
        inMemoryTaskManager.addBasicTask(task2);

        Epic epic1 = new Epic(inMemoryTaskManager.nextId(), "Эпик 1", "Описание");
        Epic epic2 = new Epic(inMemoryTaskManager.nextId(), "Эпик 2", "Описание");
        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.addEpic(epic2);

        Subtask subtask11 = new Subtask(inMemoryTaskManager.nextId(), "Подзадача 11", "Описание",
                TaskStatus.NEW, epic1.getID(),
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(60));
        Subtask subtask12 = new Subtask(inMemoryTaskManager.nextId(), "Подзадача 12", "Описание",
                TaskStatus.NEW, epic1.getID(),
                LocalDateTime.of(2025, 1, 10, 10, 0), Duration.ofMinutes(60));
        Subtask subtask13 = new Subtask(inMemoryTaskManager.nextId(), "Подзадача 13", "Описание",
                TaskStatus.NEW, epic1.getID(),
                LocalDateTime.of(2025, 1, 10, 20, 0), Duration.ofMinutes(60));
        inMemoryTaskManager.addSubtask(subtask11);
        inMemoryTaskManager.addSubtask(subtask12);
        inMemoryTaskManager.addSubtask(subtask13);

        printAllTasks(inMemoryTaskManager);
        System.out.println();

        System.out.println("=> Запросите созданные задачи несколько раз в разном порядке. " +
                "После каждого запроса выведите историю и убедитесь, что в ней нет повторов.:\n");

        System.out.println("\t-> Запрос Задача 1");
        inMemoryTaskManager.getBasicTaskById(task1.getID());
        printHistory(inMemoryTaskManager);
        System.out.println("\t-> Запрос Задача 1");
        inMemoryTaskManager.getBasicTaskById(task1.getID());
        printHistory(inMemoryTaskManager);
        System.out.println("\t-> Запрос Подзадача 11");
        inMemoryTaskManager.getSubtaskById(subtask11.getID());
        printHistory(inMemoryTaskManager);
        System.out.println("\t-> Запрос Эпик 1");
        inMemoryTaskManager.getEpicById(epic1.getID());
        printHistory(inMemoryTaskManager);
        System.out.println("\t-> Запрос Подзадача 11");
        inMemoryTaskManager.getSubtaskById(subtask11.getID());
        printHistory(inMemoryTaskManager);
        System.out.println("\t-> Запрос Задача 1");
        inMemoryTaskManager.getBasicTaskById(task1.getID());
        printHistory(inMemoryTaskManager);
        System.out.println("\t-> Запрос Подзадача 12");
        inMemoryTaskManager.getSubtaskById(subtask12.getID());
        printHistory(inMemoryTaskManager);
        System.out.println("\t-> Запрос Подзадача 13");
        inMemoryTaskManager.getSubtaskById(subtask13.getID());
        printHistory(inMemoryTaskManager);
        System.out.println("\t-> Запрос Подзадача 12");
        inMemoryTaskManager.getSubtaskById(subtask12.getID());
        printHistory(inMemoryTaskManager);

        System.out.println("=> Удалите задачу, которая есть в истории, и проверьте, " +
                "что при печати она не будет выводиться:\n");

        System.out.println("\t-> Удаление Задача 1");
        inMemoryTaskManager.removeBasicTaskById(task1.getID());
        printHistory(inMemoryTaskManager);

        System.out.println("=> Удалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик, " +
                "так и все его подзадачи:\n");

        System.out.println("\t-> Удаление Эпик 1");
        inMemoryTaskManager.removeEpicById(epic1.getID());
        printHistory(inMemoryTaskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllBasicTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Subtask subtask : manager.getAllEpicSubtasks(epic)) {
                System.out.println("--> " + subtask);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        printHistory(manager);

        System.out.println("*".repeat(20));
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
