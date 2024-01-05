package task_manager.task_manager_cli.command_parser;

import task_manager.cli_lib.argument.ArgumentList;
import task_manager.task_manager_cli.Context;
import task_manager.task_manager_cli.command.Command;
import task_manager.task_manager_cli.command.ModifyTaskCommand;

public class ModifyTaskCommandParser extends CommandParser {

    @Override
    public Command parse(Context context, ArgumentList argList) throws CommandParserException {
        ModifyTaskCommand command = new ModifyTaskCommand();

        if (!argList.getTrailingNormalArguments().isEmpty()) {
            throw new CommandParserException("Unexpected trailing arguments");
        } else if (!argList.getOptionArguments().isEmpty()) {
            throw new CommandParserException("Unexpected option arguments");
        }

        command.setModifyPropertyArgs(argList.getModifyPropertyArguments());
        command.setFilterPropertyArgs(argList.getFilterPropertyArguments());
        command.setTempIDs(ParseUtil.getTaskIDs(context, argList.getLeadingNormalArguments()));

        return command;
    }

}
