import ru.yandex.practicum.taskManagement.*;
import ru.yandex.practicum.tasks.*;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        System.out.println("=> Создание двух задач и двух эпиков (с двумя и одной подзадачами):\n");

        Task task1 = new Task(inMemoryTaskManager.nextId(), "Задача 1", "Описание", TaskStatus.NEW);
        Task task2 = new Task(inMemoryTaskManager.nextId(), "Задача 2", "Описание", TaskStatus.NEW);
        inMemoryTaskManager.addBasicTask(task1);
        inMemoryTaskManager.addBasicTask(task2);

        Epic epic1 = new Epic(inMemoryTaskManager.nextId(), "Эпик 1", "Описание", TaskStatus.NEW);
        Epic epic2 = new Epic(inMemoryTaskManager.nextId(), "Эпик 2", "Описание", TaskStatus.NEW);
        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.addEpic(epic2);

        Subtask subtask11 = new Subtask(inMemoryTaskManager.nextId(), "Подзадача 11", "Описание",
                TaskStatus.NEW, epic1.getID());
        Subtask subtask12 = new Subtask(inMemoryTaskManager.nextId(), "Подзадача 12", "Описание",
                TaskStatus.NEW, epic1.getID());
        Subtask subtask21 = new Subtask(inMemoryTaskManager.nextId(), "Подзадача 21", "Описание",
                TaskStatus.NEW, epic2.getID());
        inMemoryTaskManager.addSubtask(subtask11);
        inMemoryTaskManager.addSubtask(subtask12);
        inMemoryTaskManager.addSubtask(subtask21);

        printAllTasks(inMemoryTaskManager);
        System.out.println();

        System.out.println("=> Изменение статусов задач:\n");

        inMemoryTaskManager.updateBasicTask(new Task(task1.getID(), task1.getName(), "Новое описание",
                TaskStatus.IN_PROGRESS));
        inMemoryTaskManager.updateSubtask(new Subtask(subtask11.getID(), subtask11.getName(), "Новое описание",
                TaskStatus.IN_PROGRESS, epic1.getID()));
        inMemoryTaskManager.updateSubtask(new Subtask(subtask21.getID(), subtask21.getName(), subtask21.getDescription(),
                TaskStatus.DONE, epic2.getID()));

        printAllTasks(inMemoryTaskManager);
        System.out.println();

        System.out.println("=> Удаление одной задачи и одного эпика:\n");

        inMemoryTaskManager.removeBasicTaskById(2);
        inMemoryTaskManager.removeEpicById(3);

        printAllTasks(inMemoryTaskManager);
        System.out.println();

        System.out.println("*** s5 ***");
        System.out.println("=> Вывод пустой истории:");
        var viewedTasks = inMemoryTaskManager.getHistory();
        System.out.println(viewedTasks.size());
        inMemoryTaskManager.getBasicTaskById(1);
        inMemoryTaskManager.getSubtaskById(6);
        inMemoryTaskManager.getEpicById(4);
        inMemoryTaskManager.getBasicTaskById(1);
        System.out.println("=> Вывод непустой истории:");
        viewedTasks = inMemoryTaskManager.getHistory();
        System.out.println(viewedTasks.size());
        for (Task task : viewedTasks) {
            System.out.println("\t" + task.getID());
        }
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

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("*".repeat(20));
    }
}
