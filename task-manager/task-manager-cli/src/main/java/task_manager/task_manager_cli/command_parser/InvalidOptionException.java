
package task_manager.task_manager_cli.command_parser;

public class InvalidOptionException extends CommandParserException {

    public InvalidOptionException(String option) {
        super("Invalid option: " + option);
    }

}
