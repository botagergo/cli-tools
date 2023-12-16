package task_manager.task_manager_cli.command_parser;

import task_manager.cli_lib.argument.ArgumentList;
import task_manager.task_manager_cli.Context;
import task_manager.task_manager_cli.command.Command;
import task_manager.task_manager_cli.command.ModifyTaskCommand;

import java.util.List;

public class ModifyTaskCommandParser implements CommandParser {

    @Override
    public Command parse(Context context, ArgumentList argList) throws CommandParserException {
        if (!argList.getTrailingNormalArguments().isEmpty()) {
            throw new CommandParserException("Unexpected trailing arguments");
        } else if (!argList.getOptionArguments().isEmpty()) {
            throw new CommandParserException("Unexpected option arguments");
        }

        List<Integer> taskIDs = ParseUtil.getTaskIDs(context, argList.getLeadingNormalArguments());
        return new ModifyTaskCommand(taskIDs, argList.getFilterPropertyArguments(), argList.getModifyPropertyArguments());
    }

}
