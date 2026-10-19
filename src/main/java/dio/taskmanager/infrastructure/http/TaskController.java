package dio.taskmanager.infrastructure.http;

import dio.taskmanager.application.CreateTaskUseCase;
import dio.taskmanager.application.input.CreateTaskInput;
import dio.taskmanager.infrastructure.http.request.CreateTaskRequest;
import dio.taskmanager.infrastructure.http.response.TaskResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final CreateTaskUseCase createTaskUseCase;

    public TaskController(
            CreateTaskUseCase createTaskUseCase
    ) {
        this.createTaskUseCase = createTaskUseCase;
    }
    @PostMapping
    public TaskResponse create(@RequestBody CreateTaskRequest request) {
        var output = createTaskUseCase.execute(request.toInput());
        return TaskResponse.from(output);
    }
}