package dio.taskmanager.infrastructure.http.request;

import dio.taskmanager.application.input.UpdateTaskInput;
import dio.taskmanager.domain.TaskStatus;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Optional;

public record UpdateTaskRequest(
        Optional<@Pattern(regexp = ".*\\S.*", message = "não deve estar em branco") @Size(min = 3, max = 100) String> title,

        Optional<@Size(max = 500) String> description,
        Optional<String> status
) {
    public UpdateTaskInput toInput(){
        return new UpdateTaskInput(title, description, status.map(TaskStatus::valueOf));
    }
}
