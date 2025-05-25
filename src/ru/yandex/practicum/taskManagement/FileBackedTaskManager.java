package ru.yandex.practicum.taskManagement;

import ru.yandex.practicum.tasks.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBackedTaskManager extends InMemoryTaskManager {

    // Путь к файлу автосохранения
    private final Path autoSavePath;

    // Конструктор класса FileBackedTaskManager
    public FileBackedTaskManager(String autoSavePath) {
        super();
        this.autoSavePath = Paths.get(autoSavePath);
    }

    // Конструктор класса FileBackedTaskManager
    public FileBackedTaskManager(HistoryManager historyManager, String autoSavePath) {
        super(historyManager);
        this.autoSavePath = Paths.get(autoSavePath);
    }

    // Сохранение всех задач в файл
    private void save() {
        try (Writer writer = new FileWriter(autoSavePath.toFile())) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : getAllBasicTasks()) {
                writer.write(task.toString() + "\n");
            }

            for (Subtask subtask : getAllSubtasks()) {
                writer.write(subtask.toString() + "\n");
            }

            for (Epic epic : getAllEpics()) {
                writer.write(epic.toString() + "\n");
            }
        } catch (IOException exception) {
            throw new ManagerSaveException(exception.getMessage());
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
