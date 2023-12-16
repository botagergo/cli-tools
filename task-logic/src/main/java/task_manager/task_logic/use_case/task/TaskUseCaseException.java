package task_manager.task_logic.use_case.task;

public class TaskUseCaseException extends Exception {

    public TaskUseCaseException(String msg) {
        super(msg);
    }
    public static final String taskNotFoundMessage = "No task found with uuid '%s'";

}
