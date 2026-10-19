package dio.taskmanager.domain;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    void shouldCreateTaskWithPendingStatus() {
        Task task = new Task("Nova tarefa", Optional.empty());

        assertEquals(TaskStatus.PENDING, task.getStatus());
    }
}
