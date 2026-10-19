package dio.taskmanager.application;

import dio.taskmanager.application.input.CreateTaskInput;
import dio.taskmanager.application.output.TaskOutput;
import dio.taskmanager.domain.Task;
import dio.taskmanager.domain.TaskId;
import dio.taskmanager.domain.TaskStatus;
import dio.taskmanager.infrastructure.repository.InMemoryTaskRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateTaskUseCaseTest {

    @Test
    void shouldCreateAndPersistTask() {
        var repository = new InMemoryTaskRepository();
        var useCase = new CreateTaskUseCase(repository);
        var input = new CreateTaskInput(
                "Nova tarefa",
                Optional.of("Descrição da nova tarefa")
        );

        TaskOutput output = useCase.execute(input);

        UUID generatedId = assertDoesNotThrow(() -> UUID.fromString(output.id()));
        Task persistedTask = repository.findById(new TaskId(generatedId)).orElseThrow();
        assertAll(
                () -> assertEquals("Nova tarefa", output.title()),
                () -> assertEquals(Optional.of("Descrição da nova tarefa"), output.description()),
                () -> assertEquals("PENDING", output.status()),
                () -> assertEquals(new TaskId(generatedId), persistedTask.getId()),
                () -> assertEquals("Nova tarefa", persistedTask.getTitle()),
                () -> assertEquals(Optional.of("Descrição da nova tarefa"), persistedTask.getDescription()),
                () -> assertEquals(TaskStatus.PENDING, persistedTask.getStatus())
        );
    }
}
