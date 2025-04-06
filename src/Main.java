import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        /*Task task1 = new Task();
        System.out.println(task1.id);
        Task task2 = new Task();
        System.out.println(task2.id);*/
    }

    public static void printTaskList(ArrayList<Task> tasks) {
        for (Task task : tasks) {
            System.out.println(task);
        }
    }
}
