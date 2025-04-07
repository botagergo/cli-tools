package cli_tools.task_manager.cli.command_parser;

import cli_tools.common.cli.Context;
import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.command_parser.CommandParser;
import cli_tools.common.cli.command_parser.CommandParserException;
import cli_tools.task_manager.cli.command.AddTaskCommand;
import cli_tools.common.cli.command.Command;

public class AddTaskCommandParser extends CommandParser {

    @Override
    public Command parse(Context context, ArgumentList argList) throws CommandParserException {
        AddTaskCommand command = new AddTaskCommand();

        if (!argList.getFilterPropertyArguments().isEmpty()) {
            throw new CommandParserException("command 'add' does not accept filter property arguments");
        } else if (!argList.getLeadingPositionalArguments().isEmpty()) {
            throw new CommandParserException("command 'add' does not accept leading positional arguments");
        } else if (!argList.getOptionArguments().isEmpty()) {
            throw new CommandParserException("command 'add' does not accept option arguments");
        }

        command.setName(String.join(" ", argList.getTrailingPositionalArguments()));
        command.setModifyPropertyArgs(argList.getModifyPropertyArguments());

        return command;
    }

}
