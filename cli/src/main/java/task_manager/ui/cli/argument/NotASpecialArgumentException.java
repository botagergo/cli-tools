package task_manager.ui.cli.argument;

public class NotASpecialArgumentException extends Exception {
    public NotASpecialArgumentException(String arg) {
        super(arg);
    }
}
