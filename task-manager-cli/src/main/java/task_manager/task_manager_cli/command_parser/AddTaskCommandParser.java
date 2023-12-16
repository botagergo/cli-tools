package task_manager.task_manager_cli.command_parser;

import lombok.NonNull;
import task_manager.cli_lib.argument.ArgumentList;
import task_manager.task_manager_cli.Context;
import task_manager.task_manager_cli.command.AddTaskCommand;
import task_manager.task_manager_cli.command.Command;

public class AddTaskCommandParser implements CommandParser {

    @Override
    public Command parse(@NonNull Context context, ArgumentList argList) throws CommandParserException {
        if (!argList.getFilterPropertyArguments().isEmpty()) {
            throw new CommandParserException("Unexpected filter arguments");
        } else if (!argList.getLeadingNormalArguments().isEmpty()) {
            throw new CommandParserException("Unexpected leading arguments");
        } else if (!argList.getOptionArguments().isEmpty()) {
            throw new CommandParserException("Unexpected option arguments");
        }

        return new AddTaskCommand(
                String.join(" ", argList.getTrailingNormalArguments()),
                argList.getModifyPropertyArguments());
    }

}
