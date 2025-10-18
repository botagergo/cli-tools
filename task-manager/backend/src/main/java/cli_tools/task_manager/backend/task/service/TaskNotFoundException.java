package cli_tools.task_manager.backend.task.service;

import cli_tools.common.backend.service.ServiceException;

import java.util.UUID;

public class TaskNotFoundException extends ServiceException {

    public TaskNotFoundException(UUID taskUuid) {
        super("No task found with uuid: %s".formatted(taskUuid), null);
    }

}
