package ru.yandex.practicum.taskManagement;

import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.List;

// Класс для управления историей просмотра задач
public class InMemoryHistoryManager implements HistoryManager {

    // Список просмотренных задач
    private final ArrayList<Task> history;
    // Размер списка задач
    private static final int MAX_SIZE = 10;

    // Конструктор класса InMemoryHistoryManager
    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    // Добавить задачу в список
    @Override
    public void addTask(Task task) {
        // Проверка на null
        if (task == null) {
            return;
        }
        // Удаляем первый элемент в списке, если размер списка исчерпан
        if (history.size() == MAX_SIZE) {
            history.removeFirst();
        }

        history.add(task);

        linkLast(task);
    }

    // Вернуть список просмотренных задач
    @Override
    public List<Task> getHistory() {
        if (history.isEmpty()) {
            return List.of();
        }
        // Возвращаем новый список
        return new ArrayList<>(history);
    }

    // Удалить все вхождения задачи с указанным id из истории
    @Override
    public void removeTask(int id) {
        List<Task> tasksToRemove = new ArrayList<>();

        for (Task task : history) {
            if (task.getID() == id) {
                tasksToRemove.add(task);
            }
        }

        history.removeAll(tasksToRemove);
    }

    // Узел связного списка
    private class Node<T> {
        public T data;
        public Node<T> next;
        public Node<T> prev;

        public Node(Node<T> prev, T data, Node<T> next) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }
    // Поля связного списка
    private Node<Task> linkedListHead;
    private Node<Task> linkedListTail;
    // Методы связного списка
    // Добавить задачу в конец списка
    private void linkLast(Task task) {
        Node<Task> oldTail = linkedListTail;
        Node<Task> newTail = new Node<>(linkedListTail, task, null);
        linkedListTail = newTail;
        if (oldTail == null)
            linkedListHead = newTail;
        else
            oldTail.next = newTail;
    }
    // Собрать все задачи в List<>
    public List<Task> getTasks() {
        List<Task> listOfTasks = new ArrayList<>();

        Node<Task> next = linkedListHead;

        while(next != null) {
            listOfTasks.add(next.data);
            next = next.next;
        }

        return listOfTasks;
    }
}
