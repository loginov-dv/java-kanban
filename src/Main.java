import ru.yandex.practicum.TaskManager.TaskManager;
import ru.yandex.practicum.Tasks.Epic;
import ru.yandex.practicum.Tasks.Subtask;
import ru.yandex.practicum.Tasks.Task;
import ru.yandex.practicum.Tasks.TaskStatus;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        /*ru.yandex.practicum.Tasks.Task task1 = new ru.yandex.practicum.Tasks.Task(1, "Задача", "Описание");
        System.out.println(task1);

        ru.yandex.practicum.Tasks.Epic epic1 = new ru.yandex.practicum.Tasks.Epic(2, "Эпик", "Описание");
        ru.yandex.practicum.Tasks.Subtask subtask1 = new ru.yandex.practicum.Tasks.Subtask(3, "Подзадача", "Описание", epic1);
        epic1.addSubtask(subtask1);
        System.out.println(subtask1);
        System.out.println(epic1);*/

        TaskManager taskManager = new TaskManager();

        System.out.println("***** Creating tasks *****\n");

        Task task1 = new Task(taskManager.nextId(), "Задача 1", "Описание");
        taskManager.addBasicTask(task1);
        Task task2 = new Task(taskManager.nextId(), "Задача 2", "Описание");
        taskManager.addBasicTask(task2);

        Epic epic1 = new Epic(taskManager.nextId(), "Эпик 1", "Описание");
        taskManager.addEpicTask(epic1);
        Subtask subtask11 = new Subtask(taskManager.nextId(), "Подзадача 11", "Описание", epic1);
        taskManager.addSubtask(subtask11);

        Epic epic2 = new Epic(taskManager.nextId(), "Эпик 2", "Описание");
        taskManager.addEpicTask(epic2);
        Subtask subtask21 = new Subtask(taskManager.nextId(), "Подзадача 21", "Описание", epic2);
        taskManager.addSubtask(subtask21);
        Subtask subtask22 = new Subtask(taskManager.nextId(), "Подзадача 22", "Описание", epic2);
        taskManager.addSubtask(subtask22);

        printTasks(taskManager.getAllBasicTasks());
        printTasks(new ArrayList<Task>(taskManager.getAllEpics()));
        printTasks(new ArrayList<Task>(taskManager.getAllSubtasks()));

        System.out.println("\n***** Doing some stuff *****\n");

        task1.setStatus(TaskStatus.DONE);
        subtask11.setStatus(TaskStatus.DONE);
        subtask21.setStatus(TaskStatus.IN_PROGRESS);

        printTasks(taskManager.getAllBasicTasks());
        printTasks(new ArrayList<Task>(taskManager.getAllEpics()));
        printTasks(new ArrayList<Task>(taskManager.getAllSubtasks()));

        System.out.println("\n***** Doing some stuff again *****\n");

        taskManager.removeBasicTaskById(task2.getId());
        taskManager.removeEpicTaskById(epic2.getId());

        printTasks(taskManager.getAllBasicTasks());
        printTasks(new ArrayList<Task>(taskManager.getAllEpics()));
        printTasks(new ArrayList<Task>(taskManager.getAllSubtasks()));
    }

    private static void printTasks(ArrayList<Task> tasks) {
        for (Task task : tasks) {
            System.out.println(task);
        }
    }
}
