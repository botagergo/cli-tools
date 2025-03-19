package cli_tools.task_manager.cli.command_parser;

import lombok.NonNull;
import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.task_manager.cli.Context;
import cli_tools.task_manager.cli.command.AddTaskCommand;
import cli_tools.task_manager.cli.command.Command;

public class AddTaskCommandParser extends CommandParser {

    @Override
    public Command parse(@NonNull Context context, ArgumentList argList) throws CommandParserException {
        AddTaskCommand command = new AddTaskCommand();

        if (!argList.getFilterPropertyArguments().isEmpty()) {
            throw new CommandParserException("Unexpected filter arguments");
        } else if (!argList.getLeadingNormalArguments().isEmpty()) {
            throw new CommandParserException("Unexpected leading arguments");
        } else if (!argList.getOptionArguments().isEmpty()) {
            throw new CommandParserException("Unexpected option arguments");
        }

        command.setName(String.join(" ", argList.getTrailingNormalArguments()));
        command.setModifyPropertyArgs(argList.getModifyPropertyArguments());

        return command;
    }

}
