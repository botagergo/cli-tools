package task_manager.ui.cli.command_parser;

import task_manager.ui.cli.Context;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.Command;
import task_manager.ui.cli.command.ModifyTaskCommand;

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
