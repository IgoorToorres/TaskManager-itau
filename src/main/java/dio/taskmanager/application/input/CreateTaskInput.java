package dio.taskmanager.application.input;

import dio.taskmanager.domain.TaskStatus;

import java.util.Optional;

public record CreateTaskInput(
        String title,
        Optional<String> description
) {
}
