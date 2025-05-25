package ru.yandex.practicum.taskManagement;

import ru.yandex.practicum.exceptions.*;
import ru.yandex.practicum.tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    // Файл автосохранения
    private final File autoSaveFile;

    // Заголовок файла при чтении/сохранении
    public static final String HEADER = "id,type,name,status,description,epic";

    // Конструктор класса FileBackedTaskManager
    public FileBackedTaskManager(String autoSavePath) {
        super();
        this.autoSaveFile = new File(autoSavePath);
    }

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
            throw new ManagerSaveException("Ошибка при сохранении файла. " + exception.getMessage());
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
            while(bufferedReader.ready()) {
                String line = bufferedReader.readLine();

                if (line.isBlank() || line.startsWith("id")) {
                    continue;
                }

                String[] args = line.split(",");
                String type = args[1];

                switch (type) {
                    case "Task" :
                        Task task = Task.fromString(line);
                        manager.addBasicTask(task);
                        break;
                    case "Subtask" :
                        Subtask subtask = Subtask.fromString(line);
                        manager.addSubtask(subtask);
                        break;
                    case "Epic" :
                        Epic epic = Epic.fromString(line);
                        manager.addEpic(epic);
                        break;
                    default:
                        throw new IllegalArgumentException("Неизвестный тип задачи");
                }
            }
        } catch (IOException exception) {
            throw new ManagerLoadException("Ошибка при чтении файла. " + exception.getMessage());
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
}
