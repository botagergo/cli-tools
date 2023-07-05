package task_manager.ui.cli.command_parser;

import task_manager.ui.cli.Context;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.Command;
import task_manager.ui.cli.command.DeleteTaskCommand;

import java.util.List;

public class DeleteTaskCommandParser implements CommandParser {

    @Override
    public Command parse(Context context, ArgumentList argList) throws CommandParserException {
        List<Integer> taskIDs = ParseUtil.getTaskIDs(context, argList.getLeadingNormalArguments());
        return new DeleteTaskCommand(taskIDs, argList.getFilterPropertyArguments());
    }

}
