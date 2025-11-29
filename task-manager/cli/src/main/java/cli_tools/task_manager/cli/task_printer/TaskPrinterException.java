package cli_tools.task_manager.cli.task_printer;

public class TaskPrinterException extends Exception {
    public TaskPrinterException(String msg, Exception e) {
        super(msg, e);
    }

    public TaskPrinterException(String msg) {
        super(msg);
    }
}
