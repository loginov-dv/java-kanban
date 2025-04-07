import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        /*Task task1 = new Task(1, "Задача", "Описание");
        System.out.println(task1);

        Epic epic1 = new Epic(2, "Эпик", "Описание");
        Subtask subtask1 = new Subtask(3, "Подзадача", "Описание", epic1);
        epic1.addSubtask(subtask1);
        System.out.println(subtask1);
        System.out.println(epic1);*/

        System.out.println("***** Creating tasks *****\n");

        Task task1 = new Task(1, "Задача 1", "Описание");
        Task task2 = new Task(2, "Задача 2", "Описание");

        Epic epic1 = new Epic(3, "Эпик 1", "Описание");
        Subtask subtask11 = new Subtask(4, "Подзадача 11", "Описание", epic1);

        Epic epic2 = new Epic(5, "Эпик 2", "Описание");
        Subtask subtask21 = new Subtask(6, "Подзадача 21", "Описание", epic2);
        Subtask subtask22 = new Subtask(7, "Подзадача 22", "Описание", epic2);

        ArrayList<Task> tasks = new ArrayList<>(List.of(task1, task2, epic1, subtask11, epic2, subtask21, subtask22));

        for (Task task : tasks) {
            System.out.println(task);
        }

        System.out.println("***** Doing some stuff *****\n");

        task1.setStatus(TaskStatus.DONE);
        subtask11.setStatus(TaskStatus.DONE);
        subtask21.setStatus(TaskStatus.IN_PROGRESS);

        for (Task task : tasks) {
            System.out.println(task);
        }
    }
}
