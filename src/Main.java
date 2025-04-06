import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        Task task1 = new Task(1, "Задача", "Описание");
        System.out.println(task1);

        Epic epic1 = new Epic(2, "Эпик", "Описание");
        Subtask subtask1 = new Subtask(3, "Подзадача", "Описание", epic1);
        epic1.addSubtask(subtask1);
        System.out.println(subtask1);
        System.out.println(epic1);
    }

    /*public static void printTaskList(ArrayList<Task> tasks) {
        for (Task task : tasks) {
            System.out.println(task);
        }
    }*/
}
