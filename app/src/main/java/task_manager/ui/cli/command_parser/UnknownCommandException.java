package task_manager.ui.cli.command_parser;

public class UnknownCommandException extends Exception {
    public UnknownCommandException(String commandName) {
        super("Unknown command: " + commandName);
    }
}
