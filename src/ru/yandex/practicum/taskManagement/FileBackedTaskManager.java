package ru.yandex.practicum.taskManagement;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ru.yandex.practicum.exceptions.*;
import ru.yandex.practicum.tasks.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    // Файл автосохранения
    private final File autoSaveFile;

    // Заголовок файла при чтении/сохранении
    public static final String HEADER = "id,type,name,status,description,startTime,duration,epic";

    // Конструктор класса FileBackedTaskManager
    public FileBackedTaskManager(File autoSaveFile) {
        super();
        this.autoSaveFile = autoSaveFile;
    }

    // Сохранение всех задач в файл
    private void save() {
        try (Writer writer = new FileWriter(autoSaveFile, StandardCharsets.UTF_8)) {
            writer.write(HEADER + "\n");

            List<Task> allTasks = new ArrayList<>();
            allTasks.addAll(getAllBasicTasks());
            allTasks.addAll(getAllEpics());
            allTasks.addAll(getAllSubtasks());

            for (Task task : allTasks) {
                writer.write(task.toString() + "\n");
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка при сохранении файла. " + exception.getMessage(), exception);
        }
    }

    // Создать трекер, загрузив задачи из файла
    public static FileBackedTaskManager loadFromFile(File fromFile, File autoSaveFile) {
        FileBackedTaskManager manager = new FileBackedTaskManager(autoSaveFile);
        loadTasksFromFile(manager, fromFile);

        return manager;
    }

    // Загрузить в трекер задачи из файла
    private static void loadTasksFromFile(FileBackedTaskManager manager, File fromFile) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fromFile, StandardCharsets.UTF_8))) {
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();

                if (line.isBlank() || line.startsWith("id")) {
                    continue;
                }

                // Всё, что находится за вторым элементом массива, на данном этапе не имеет значения
                String[] args = line.split(",", 3);
                // Тип задачи
                TaskType type = TaskType.TASK.valueOf(args[1].toUpperCase());
                // Изменяем globalId в трекере для корректного присвоения идентификаторов новым задачам (не из файла)
                int maxId = Integer.parseInt(args[0]);
                if (maxId >= manager.globalID) {
                    manager.globalID = maxId;
                }

                switch (type) {
                    case TASK:
                        Task task = Task.fromString(line);
                        manager.addBasicTask(task);
                        break;
                    case SUBTASK:
                        Subtask subtask = Subtask.fromString(line);
                        manager.addSubtask(subtask);
                        break;
                    case EPIC:
                        Epic epic = Epic.fromString(line);
                        manager.addEpic(epic);
                        break;
                    default:
                        throw new IllegalArgumentException("Неизвестный тип задачи");
                }
            }
        } catch (IOException | IllegalArgumentException | IndexOutOfBoundsException exception) {
            throw new ManagerLoadException("Ошибка при чтении файла. " + exception.getMessage(), exception);
        }
    }

    // Добавление новой задачи (обычной)
    @Override
    public void addBasicTask(Task task) {
        super.addBasicTask(task);
        save();
    }

    // Добавление новой поздадачи
    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    // Добавление нового эпика
    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    // Обновление задачи (обычной)
    @Override
    public void updateBasicTask(Task updatedTask) {
        super.updateBasicTask(updatedTask);
        save();
    }

    // Обновление подзадачи
    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        super.updateSubtask(updatedSubtask);
        save();
    }

    // Обновление эпика
    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public void removeBasicTaskById(int id) {
        super.removeBasicTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    // Удаление всех задач (обычных)
    @Override
    public void removeAllBasicTasks() {
        super.removeAllBasicTasks();
        save();
    }

    // Удаление всех подзадач
    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    // Удаление всех эпиков
    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    // Пользовательский сценарий
    public static void main(String[] args) throws IOException {
        // Создаём трекер
        File saveFile = File.createTempFile("test1", ".csv");
        saveFile.deleteOnExit();
        FileBackedTaskManager taskManager = new FileBackedTaskManager(saveFile);

        // Добавляем задачи
        Task task1 = new Task(taskManager.nextId(), "Задача 1", "Описание", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(60));
        Task task2 = new Task(taskManager.nextId(), "Задача 2", "Описание", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(60));
        taskManager.addBasicTask(task1);
        taskManager.addBasicTask(task2);

        Epic epic1 = new Epic(taskManager.nextId(), "Эпик 1", "Описание", TaskStatus.NEW,
                null, Duration.ZERO);
        Epic epic2 = new Epic(taskManager.nextId(), "Эпик 2", "Описание", TaskStatus.NEW,
                null, Duration.ZERO);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Subtask subtask11 = new Subtask(taskManager.nextId(), "Подзадача 11", "Описание",
                TaskStatus.NEW, epic1.getID(),
                LocalDateTime.now(), Duration.ofMinutes(60));
        Subtask subtask12 = new Subtask(taskManager.nextId(), "Подзадача 12", "Описание",
                TaskStatus.NEW, epic1.getID(),
                LocalDateTime.now(), Duration.ofMinutes(60));
        Subtask subtask13 = new Subtask(taskManager.nextId(), "Подзадача 13", "Описание",
                TaskStatus.NEW, epic1.getID(),
                LocalDateTime.now(), Duration.ofMinutes(60));
        taskManager.addSubtask(subtask11);
        taskManager.addSubtask(subtask12);
        taskManager.addSubtask(subtask13);

        // Создаём новый трекер из файла другого трекера
        File anotherSaveFile = File.createTempFile("test1", ".csv");
        anotherSaveFile.deleteOnExit();
        FileBackedTaskManager newTaskManager = FileBackedTaskManager.loadFromFile(saveFile, anotherSaveFile);

        // Проверяем наличие всех задач
        System.out.println(newTaskManager.getBasicTaskById(task1.getID()));
        System.out.println(newTaskManager.getBasicTaskById(task2.getID()));
        System.out.println(newTaskManager.getEpicById(epic1.getID()));
        System.out.println(newTaskManager.getEpicById(epic2.getID()));
        System.out.println(newTaskManager.getSubtaskById(subtask11.getID()));
        System.out.println(newTaskManager.getSubtaskById(subtask12.getID()));
        System.out.println(newTaskManager.getSubtaskById(subtask13.getID()));
    }
}
