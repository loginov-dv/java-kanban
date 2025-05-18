package ru.yandex.practicum.taskManagement;

import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Класс для управления историей просмотра задач
public class InMemoryHistoryManager implements HistoryManager {
    // Первый элемент в связном списке задач
    private Node<Task> head;
    // Последний элемент в связном списке задач
    private Node<Task> tail;
    // Мапа с id задач и соответствующими узлами
    private final Map<Integer, Node<Task>> nodes = new HashMap<>();

    // Конструктор класса InMemoryHistoryManager
    public InMemoryHistoryManager() {

    }

    // Добавить задачу в список
    @Override
    public void addTask(Task task) {
        // Проверка задачи на null
        if (task == null) {
            return;
        }
        // Создаём новый узел в конце связного списка
        Node<Task> node = linkLast(task);
        // Удаляем старый узел (при наличии)
        if (nodes.containsKey(task.getID())) {
            Node<Task> nodeToRemove = nodes.get(task.getID());
            removeNode(nodeToRemove);
        }
        // Обновляем узел в хешмапе
        nodes.put(task.getID(), node);
    }

    // Вернуть список просмотренных задач
    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    // Удалить задачу из истории
    @Override
    public void removeTask(int id) {
        if (!nodes.containsKey(id)) {
            return;
        }
        // Находим связанный узел
        Node<Task> nodeToRemove = nodes.remove(id);
        // Удаляем узел
        removeNode(nodeToRemove);
    }

    // Узел связного списка
    private static class Node<T> {
        // Объект, который хранится в узле
        private T data;
        // Следующий узел
        private Node<T> next;
        // Предыдущий узел
        private Node<T> prev;

        // Конструктор класса Node<T>
        public Node(Node<T> prev, T data, Node<T> next) {
            setData(data);
            setNext(next);
            setPrev(prev);
        }

        public T getData() {
            return data;
        }

        public void setData(T value) {
            data = value;
        }

        public Node<T> getNext() {
            return next;
        }

        public void setNext(Node<T> value) {
            next = value;
        }

        public Node<T> getPrev() {
            return prev;
        }

        public void setPrev(Node<T> value) {
            prev = value;
        }
    }

    // Добавить задачу в конец списка
    private Node<Task> linkLast(Task task) {
        Node<Task> oldTail = tail;
        Node<Task> newTail = new Node<>(tail, task, null);
        tail = newTail;
        // Если список был пуст, то новый узел будет являться head
        if (oldTail == null) {
            head = newTail;
        } else {
            oldTail.next = newTail;
        }

        return newTail;
    }

    // Собрать все задачи в List<>
    private List<Task> getTasks() {
        List<Task> listOfTasks = new ArrayList<>();
        Node<Task> next = head;

        while (next != null) {
            listOfTasks.add(next.data);
            next = next.next;
        }

        return listOfTasks;
    }

    // Удалить узел
    private void removeNode(Node<Task> node) {
        Node<Task> prev = node.getPrev();
        Node<Task> next = node.getNext();
        // Если удаляемый node является head
        if (prev == null) {
            head = next;
        } else {
            prev.setNext(next);
            node.setPrev(null);
        }
        // Если удаляемый node является tail
        if (next == null) {
            tail = prev;
        } else {
            next.setPrev(prev);
            node.setNext(null);
        }

        node.setData(null);
    }
}
