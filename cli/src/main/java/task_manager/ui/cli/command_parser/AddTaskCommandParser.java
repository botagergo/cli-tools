package task_manager.ui.cli.command_parser;

import lombok.NonNull;
import task_manager.ui.cli.Context;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.AddTaskCommand;
import task_manager.ui.cli.command.Command;

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
