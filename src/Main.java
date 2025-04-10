import ru.yandex.practicum.TaskManager.TaskManager;
import ru.yandex.practicum.Tasks.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        System.out.println("=> Создание двух задач и двух эпиков (с двумя и одной подзадачами):\n");

        Task task1 = new Task(taskManager.nextId(), "Задача 1", "Описание", TaskStatus.NEW);
        Task task2 = new Task(taskManager.nextId(), "Задача 2", "Описание", TaskStatus.NEW);
        taskManager.addBasicTask(task1);
        taskManager.addBasicTask(task2);

        Epic epic1 = new Epic(taskManager.nextId(), "Эпик 1", "Описание", TaskStatus.NEW);
        Epic epic2 = new Epic(taskManager.nextId(), "Эпик 2", "Описание", TaskStatus.NEW);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Subtask subtask11 = new Subtask(taskManager.nextId(), "Подзадача 11", "Описание",
                TaskStatus.NEW, epic1.getId());
        Subtask subtask12 = new Subtask(taskManager.nextId(), "Подзадача 12", "Описание",
                TaskStatus.NEW, epic1.getId());
        Subtask subtask21 = new Subtask(taskManager.nextId(), "Подзадача 21", "Описание",
                TaskStatus.NEW, epic2.getId());
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        taskManager.addSubtask(subtask21);

        printTasks(taskManager.getAllBasicTasks());
        printTasks(new ArrayList<>(taskManager.getAllEpics()));
        printTasks(new ArrayList<>(taskManager.getAllSubtasks()));
        System.out.println();

        System.out.println("=> Изменение статусов задач:\n");

        taskManager.updateBasicTask(new Task(task1.getId(), task1.getName(), "Новое описание",
                TaskStatus.IN_PROGRESS));
        taskManager.updateSubtask(new Subtask(subtask11.getId(), subtask11.getName(), "Новое описание",
                TaskStatus.IN_PROGRESS, epic1.getId()));
        taskManager.updateSubtask(new Subtask(subtask21.getId(), subtask21.getName(), subtask21.getDescription(),
                TaskStatus.DONE, epic2.getId()));

        printTasks(taskManager.getAllBasicTasks());
        printTasks(new ArrayList<>(taskManager.getAllEpics()));
        printTasks(new ArrayList<>(taskManager.getAllSubtasks()));
        System.out.println();

        System.out.println("=> Удаление одной задачи и одного эпика:\n");

        taskManager.removeBasicTaskById(2);
        taskManager.removeEpicById(3);

        printTasks(taskManager.getAllBasicTasks());
        printTasks(new ArrayList<>(taskManager.getAllEpics()));
        printTasks(new ArrayList<>(taskManager.getAllSubtasks()));
        System.out.println();
    }

    private static void printTasks(List<Task> tasks) {
        for (Task task : tasks) {
            System.out.println(task);
        }
    }
}
