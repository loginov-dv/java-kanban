import ru.yandex.practicum.taskManagement.InMemoryTaskManager;
import ru.yandex.practicum.tasks.*;

import java.util.ArrayList;
import java.util.List;

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
                TaskStatus.NEW, epic1.getId());
        Subtask subtask12 = new Subtask(inMemoryTaskManager.nextId(), "Подзадача 12", "Описание",
                TaskStatus.NEW, epic1.getId());
        Subtask subtask21 = new Subtask(inMemoryTaskManager.nextId(), "Подзадача 21", "Описание",
                TaskStatus.NEW, epic2.getId());
        inMemoryTaskManager.addSubtask(subtask11);
        inMemoryTaskManager.addSubtask(subtask12);
        inMemoryTaskManager.addSubtask(subtask21);

        printTasks(inMemoryTaskManager.getAllBasicTasks());
        printTasks(new ArrayList<>(inMemoryTaskManager.getAllEpics()));
        printTasks(new ArrayList<>(inMemoryTaskManager.getAllSubtasks()));
        System.out.println();

        System.out.println("=> Изменение статусов задач:\n");

        inMemoryTaskManager.updateBasicTask(new Task(task1.getId(), task1.getName(), "Новое описание",
                TaskStatus.IN_PROGRESS));
        inMemoryTaskManager.updateSubtask(new Subtask(subtask11.getId(), subtask11.getName(), "Новое описание",
                TaskStatus.IN_PROGRESS, epic1.getId()));
        inMemoryTaskManager.updateSubtask(new Subtask(subtask21.getId(), subtask21.getName(), subtask21.getDescription(),
                TaskStatus.DONE, epic2.getId()));

        printTasks(inMemoryTaskManager.getAllBasicTasks());
        printTasks(new ArrayList<>(inMemoryTaskManager.getAllEpics()));
        printTasks(new ArrayList<>(inMemoryTaskManager.getAllSubtasks()));
        System.out.println();

        System.out.println("=> Удаление одной задачи и одного эпика:\n");

        inMemoryTaskManager.removeBasicTaskById(2);
        inMemoryTaskManager.removeEpicById(3);

        printTasks(inMemoryTaskManager.getAllBasicTasks());
        printTasks(new ArrayList<>(inMemoryTaskManager.getAllEpics()));
        printTasks(new ArrayList<>(inMemoryTaskManager.getAllSubtasks()));
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
            System.out.println("\t" + task.getId());
        }
    }

    private static void printTasks(List<Task> tasks) {
        for (Task task : tasks) {
            System.out.println(task);
        }
    }
}
