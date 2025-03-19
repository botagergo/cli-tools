package cli_tools.task_manager.task.service;

public class TaskServiceException extends Exception {

    public TaskServiceException(String msg) {
        super(msg);
    }
    public static final String taskNotFoundMessage = "No task found with uuid '%s'";

}
