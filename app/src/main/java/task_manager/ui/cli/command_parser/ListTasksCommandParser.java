package task_manager.ui.cli.command_parser;

import task_manager.api.command.Command;
import task_manager.api.command.ListTasksCommand;

public class ListTasksCommandParser implements CommandParser {

    @Override
    public Command parse(ArgumentList argList) {
        return new ListTasksCommand();
    }

}
