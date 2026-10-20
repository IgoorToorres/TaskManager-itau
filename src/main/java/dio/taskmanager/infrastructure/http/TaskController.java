package dio.taskmanager.infrastructure.http;

import dio.taskmanager.application.*;
import dio.taskmanager.application.input.CreateTaskInput;
import dio.taskmanager.domain.TaskId;
import dio.taskmanager.infrastructure.http.request.CreateTaskRequest;
import dio.taskmanager.infrastructure.http.request.UpdateTaskRequest;
import dio.taskmanager.infrastructure.http.response.TaskResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final CreateTaskUseCase createTaskUseCase;
    private final GetTasksUseCase getTasksUseCase;
    private final GetTaskByIdUseCase getTaskByIdUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;

    public TaskController(
            CreateTaskUseCase createTaskUseCase,
            GetTasksUseCase getTasksUseCase,
            GetTaskByIdUseCase getTaskByIdUseCase,
            DeleteTaskUseCase deleteTaskUseCase,
            UpdateTaskUseCase updateTaskUseCase
    ) {
        this.createTaskUseCase = createTaskUseCase;
        this.getTasksUseCase = getTasksUseCase;
        this.getTaskByIdUseCase = getTaskByIdUseCase;
        this.deleteTaskUseCase = deleteTaskUseCase;
        this.updateTaskUseCase = updateTaskUseCase;
    }


    @PostMapping
    public TaskResponse create(@RequestBody @Valid CreateTaskRequest request) {
        var output = createTaskUseCase.execute(request.toInput());
        return TaskResponse.from(output);
    }

    @GetMapping
    public List<TaskResponse> list() {
        return getTasksUseCase.execute().stream().map(TaskResponse::from).toList();
    }

    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable UUID id) {
        var output = getTaskByIdUseCase.execute(new TaskId(id));
        return TaskResponse.from(output);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        deleteTaskUseCase.execute(new TaskId(id));
    }

    @PatchMapping("/{id}")
    public TaskResponse update(@PathVariable UUID id, @RequestBody @Valid UpdateTaskRequest request){
        var input = request.toInput();
        var output = updateTaskUseCase.execute(new TaskId(id), input);
        return TaskResponse.from(output);
    }
}