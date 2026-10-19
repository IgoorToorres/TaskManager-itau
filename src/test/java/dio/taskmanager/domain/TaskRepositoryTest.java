package dio.taskmanager.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskRepositoryTest {

    private TaskRepository repository;

    protected abstract TaskRepository createRepository();

    @BeforeEach
    void setUp() {
        repository = createRepository();
    }

    @Test
    void shouldStartEmpty() {
        List<Task> tasks = repository.findAll();

        assertTrue(tasks.isEmpty());
    }

    @Test
    void shouldSaveAndFindTaskById() {
        Task task = new Task(
                "Implementar testes do repositório",
                Optional.of("Criar testes de contrato reutilizáveis"),
                TaskStatus.PENDING
        );

        Task savedTask = repository.save(task);
        Optional<Task> foundTask = repository.findById(task.getId());

        assertEquals(task.getId(), savedTask.getId());
        assertTrue(foundTask.isPresent());
        assertAll(
                () -> assertEquals(task.getId(), foundTask.orElseThrow().getId()),
                () -> assertEquals(task.getTitle(), foundTask.orElseThrow().getTitle()),
                () -> assertEquals(task.getDescription(), foundTask.orElseThrow().getDescription()),
                () -> assertEquals(task.getStatus(), foundTask.orElseThrow().getStatus())
        );
    }

    @Test
    void shouldReturnEmptyWhenTaskDoesNotExist() {
        Optional<Task> task = repository.findById(new TaskId());

        assertTrue(task.isEmpty());
    }

    @Test
    void shouldFindAllSavedTasks() {
        Task firstTask = new Task("Primeira tarefa", Optional.empty(), TaskStatus.PENDING);
        Task secondTask = new Task("Segunda tarefa", Optional.empty(), TaskStatus.PENDING);
        repository.save(firstTask);
        repository.save(secondTask);

        Set<TaskId> foundIds = repository.findAll().stream()
                .map(Task::getId)
                .collect(Collectors.toSet());

        assertEquals(Set.of(firstTask.getId(), secondTask.getId()), foundIds);
    }

    @Test
    void shouldReplaceTaskWhenSavingTheSameId() {
        Task originalTask = new Task("Título original", Optional.empty(), TaskStatus.PENDING);
        repository.save(originalTask);

        Task updatedTask = new Task(
                "Título atualizado",
                Optional.of("Descrição atualizada"),
                TaskStatus.COMPLETED
        );
        updatedTask.setId(originalTask.getId());
        updatedTask.setStatus(TaskStatus.COMPLETED);

        repository.save(updatedTask);

        List<Task> tasks = repository.findAll();
        Task foundTask = repository.findById(originalTask.getId()).orElseThrow();
        assertAll(
                () -> assertEquals(1, tasks.size()),
                () -> assertEquals("Título atualizado", foundTask.getTitle()),
                () -> assertEquals(Optional.of("Descrição atualizada"), foundTask.getDescription()),
                () -> assertEquals(TaskStatus.COMPLETED, foundTask.getStatus())
        );
    }

    @Test
    void shouldDeleteTaskById() {
        Task task = new Task("Tarefa a excluir", Optional.empty(), TaskStatus.PENDING);
        repository.save(task);

        repository.delete(task.getId());

        assertTrue(repository.findById(task.getId()).isEmpty());
        assertTrue(repository.findAll().isEmpty());
    }

}
