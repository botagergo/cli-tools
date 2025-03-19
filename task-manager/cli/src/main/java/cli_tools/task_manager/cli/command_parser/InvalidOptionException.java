
package cli_tools.task_manager.cli.command_parser;

public class InvalidOptionException extends CommandParserException {

    public InvalidOptionException(String option) {
        super("Invalid option: " + option);
    }

}
