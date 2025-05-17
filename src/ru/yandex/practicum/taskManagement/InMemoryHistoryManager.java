package ru.yandex.practicum.taskManagement;

import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Класс для управления историей просмотра задач
public class InMemoryHistoryManager implements HistoryManager {

    // Список просмотренных задач
    //private final ArrayList<Task> history;
    // Размер списка задач
    //private static final int MAX_SIZE = 10;

    // Конструктор класса InMemoryHistoryManager
    public InMemoryHistoryManager() {
        //history = new ArrayList<>();
    }

    // Добавить задачу в список
    @Override
    public void addTask(Task task) {
        // Проверка на null
        if (task == null) {
            return;
        }
        // Удаляем первый элемент в списке, если размер списка исчерпан
        /*if (history.size() == MAX_SIZE) {
            history.removeFirst();
        }*/

        //history.add(task);

        Node<Task> node = linkLast(task);

        if (map.containsKey(task.getID())) {
            Node<Task> nodeToRemove = map.get(task.getID());
            removeNode(nodeToRemove);
        }

        map.put(task.getID(), node);
    }

    // Вернуть список просмотренных задач
    @Override
    public List<Task> getHistory() {
        /*if (history.isEmpty()) {
            return List.of();
        }
        // Возвращаем новый список
        return new ArrayList<>(history);*/

        return getTasks();
    }

    // Удалить все вхождения задачи с указанным id из истории
    @Override
    public void removeTask(int id) {
        /*List<Task> tasksToRemove = new ArrayList<>();

        for (Task task : history) {
            if (task.getID() == id) {
                tasksToRemove.add(task);
            }
        }

        history.removeAll(tasksToRemove);*/

        if (!map.containsKey(id)) {
            return;
        }

        Node<Task> nodeToRemove = map.remove(id);

        removeNode(nodeToRemove);
    }

    // Узел связного списка
    private class Node<T> {
        public T data;
        public Node<T> next;
        public Node<T> prev;

        public Node(Node<T> prev, T data, Node<T> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }
    // Поля связного списка
    private Node<Task> linkedListHead;
    private Node<Task> linkedListTail;
    public int linkedListSize = 0;
    // Методы связного списка
    // Добавить задачу в конец списка
    private Node<Task> linkLast(Task task) {
        Node<Task> oldTail = linkedListTail;
        Node<Task> newTail = new Node<>(linkedListTail, task, null);
        linkedListTail = newTail;
        if (oldTail == null)
            linkedListHead = newTail;
        else
            oldTail.next = newTail;
        linkedListSize++;

        return newTail;
    }
    // Собрать все задачи в List<>
    private List<Task> getTasks() {
        List<Task> listOfTasks = new ArrayList<>();

        Node<Task> next = linkedListHead;

        while(next != null) {
            listOfTasks.add(next.data);
            next = next.next;
        }

        return listOfTasks;
    }
    // Удалить узел
    private void removeNode(Node<Task> node) {
        Node<Task> prev = node.prev;
        Node<Task> next = node.next;
        // Если удаляемый node является head
        if (prev == null) {
            linkedListHead = next;
        } else {
            prev.next = next;
        }
        // Если удаляемый node является tail
        if (next == null) {
            linkedListTail = prev;
        } else {
            next.prev = prev;
        }
    }
    // Хешмапа
    private Map<Integer, Node<Task>> map = new HashMap<>();
}
