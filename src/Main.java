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

        Epic epic1 = new Epic(taskManager.nextId(), "Эпик 1", "Описание", TaskStatus.NEW);
        Epic epic2 = new Epic(taskManager.nextId(), "Эпик 2", "Описание", TaskStatus.NEW);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Task task1 = new Task(taskManager.nextId(), "Задача 1", "Описание", TaskStatus.NEW);
        Task task2 = new Task(taskManager.nextId(), "Задача 2", "Описание", TaskStatus.NEW);
        taskManager.addBasicTask(task1);
        taskManager.addBasicTask(task2);

        printTasks(taskManager.getAllBasicTasks());
        printTasks(new ArrayList<Task>(taskManager.getAllEpics()));
        printTasks(new ArrayList<Task>(taskManager.getAllSubtasks()));
        System.out.println();

        Subtask subtask11 = new Subtask(taskManager.nextId(), "Подзадача 11", "Описание",
                TaskStatus.NEW, epic1);
        Subtask subtask21 = new Subtask(taskManager.nextId(), "Подзадача 21", "Описание",
                TaskStatus.NEW, epic2);
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask21);

        printTasks(taskManager.getAllBasicTasks());
        printTasks(new ArrayList<Task>(taskManager.getAllEpics()));
        printTasks(new ArrayList<Task>(taskManager.getAllSubtasks()));
        System.out.println();

        epic1 = taskManager.getEpicById(1);
        Subtask subtask11new = new Subtask(subtask11.getId(), subtask11.getName(), subtask11.getDescription(), TaskStatus.DONE, epic1);
        taskManager.updateSubtask(subtask11new);

        printTasks(taskManager.getAllBasicTasks());
        printTasks(new ArrayList<Task>(taskManager.getAllEpics()));
        printTasks(new ArrayList<Task>(taskManager.getAllSubtasks()));
        System.out.println();

        epic1 = taskManager.getEpicById(1);
        Subtask subtask12 = new Subtask(taskManager.nextId(), "Подзадача 12", "Описание",
                TaskStatus.IN_PROGRESS, epic1);
        taskManager.addSubtask(subtask12);

        printTasks(taskManager.getAllBasicTasks());
        printTasks(new ArrayList<Task>(taskManager.getAllEpics()));
        printTasks(new ArrayList<Task>(taskManager.getAllSubtasks()));
        System.out.println();

        epic1 = taskManager.getEpicById(1);
        Subtask subtask13 = new Subtask(taskManager.nextId(), "Подзадача 13", "Описание",
                TaskStatus.DONE, epic1);
        taskManager.addSubtask(subtask13);

        printTasks(taskManager.getAllBasicTasks());
        printTasks(new ArrayList<Task>(taskManager.getAllEpics()));
        printTasks(new ArrayList<Task>(taskManager.getAllSubtasks()));
        System.out.println();

        taskManager.removeSubtaskById(7);

        printTasks(taskManager.getAllBasicTasks());
        printTasks(new ArrayList<Task>(taskManager.getAllEpics()));
        printTasks(new ArrayList<Task>(taskManager.getAllSubtasks()));
        System.out.println();

        taskManager.removeEpicById(2);

        printTasks(taskManager.getAllBasicTasks());
        printTasks(new ArrayList<Task>(taskManager.getAllEpics()));
        printTasks(new ArrayList<Task>(taskManager.getAllSubtasks()));
        System.out.println();

    }

    private static void printTasks(ArrayList<Task> tasks) {
        for (Task task : tasks) {
            System.out.println(task);
        }
    }
}
