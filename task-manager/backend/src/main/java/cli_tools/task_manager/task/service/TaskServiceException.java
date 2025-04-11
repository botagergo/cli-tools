package cli_tools.task_manager.task.service;

public class TaskServiceException extends Exception {

    public static final String taskNotFoundMessage = "No task found with uuid '%s'";

    public TaskServiceException(String msg) {
        super(msg);
    }

}
