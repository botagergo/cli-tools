package task_manager.task_manager_cli.command_parser;

import task_manager.cli_lib.argument.ArgumentList;
import task_manager.task_manager_cli.Context;
import task_manager.task_manager_cli.command.Command;
import task_manager.task_manager_cli.command.DoneTaskCommand;

public class DoneTaskCommandParser extends CommandParser {

    @Override
    public Command parse(Context context, ArgumentList argList) throws CommandParserException {
        DoneTaskCommand command = new DoneTaskCommand();

        if (!argList.getModifyPropertyArguments().isEmpty()) {
            throw new CommandParserException("Unexpected property arguments");
        } else if (!argList.getTrailingNormalArguments().isEmpty()) {
            throw new CommandParserException("Unexpected trailing arguments");
        } else if (!argList.getOptionArguments().isEmpty()) {
            throw new CommandParserException("Unexpected option arguments");
        }

        command.setTempIDs(ParseUtil.getTaskIDs(context, argList.getLeadingNormalArguments()));
        command.setFilterPropertyArgs(argList.getFilterPropertyArguments());

        return command;
    }

}
