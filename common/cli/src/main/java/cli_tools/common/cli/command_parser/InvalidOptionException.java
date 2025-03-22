
package cli_tools.common.cli.command_parser;

public class InvalidOptionException extends CommandParserException {

    public InvalidOptionException(String option) {
        super("Invalid option: " + option);
    }

}
