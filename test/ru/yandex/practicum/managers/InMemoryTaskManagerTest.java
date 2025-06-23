package ru.yandex.practicum.managers;

import org.junit.jupiter.api.BeforeEach;

// Класс, содержащий тесты, относящиеся к специфическим методам InMemoryTaskManager
class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    protected void beforeEach() {
        taskManager = Managers.getInMemoryTaskManager();
    }
}