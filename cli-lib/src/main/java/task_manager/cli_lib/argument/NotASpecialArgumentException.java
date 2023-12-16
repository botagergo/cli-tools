package task_manager.cli_lib.argument;

public class NotASpecialArgumentException extends Exception {
    public NotASpecialArgumentException(String arg) {
        super(arg);
    }
}
