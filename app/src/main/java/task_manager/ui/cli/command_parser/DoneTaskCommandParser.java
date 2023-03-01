package task_manager.ui.cli.command_parser;

import task_manager.api.command.Command;
import task_manager.api.command.DoneTaskCommand;

public class DoneTaskCommandParser implements CommandParser {

    @Override
    public Command parse(ArgumentList argList) {
        String query = String.join(" ", argList.normalArguments);
        return new DoneTaskCommand(query);
    }

}
