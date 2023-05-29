package task_manager.ui.cli.command_parser;

import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.Command;
import task_manager.ui.cli.command.DoneTaskCommand;

public class DoneTaskCommandParser implements CommandParser {

    @Override
    public Command parse(ArgumentList argList) {
        String query = String.join(" ", argList.getNormalArguments());
        return new DoneTaskCommand(query);
    }

}
