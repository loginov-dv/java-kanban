package ru.yandex.practicum.taskManagement;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    protected void beforeEach() {
        taskManager = Managers.getInMemoryTaskManager();
    }
}