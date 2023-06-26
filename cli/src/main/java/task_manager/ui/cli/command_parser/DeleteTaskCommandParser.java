package task_manager.ui.cli.command_parser;

import task_manager.ui.cli.Util;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.Command;
import task_manager.ui.cli.command.DeleteTaskCommand;

import java.util.ArrayList;
import java.util.List;

public class DeleteTaskCommandParser implements CommandParser {

    @Override
    public Command parse(ArgumentList argList) throws CommandParserException {
        List<Integer> taskIDs = new ArrayList<>();
        for (String arg : argList.getNormalArguments()) {
            taskIDs.add(Util.parseTaskID(arg));
        }
        return new DeleteTaskCommand(taskIDs);
    }

}
