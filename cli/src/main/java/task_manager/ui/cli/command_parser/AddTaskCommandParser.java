package task_manager.ui.cli.command_parser;

import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.AddTaskCommand;
import task_manager.ui.cli.command.Command;

public class AddTaskCommandParser implements CommandParser {

    @Override
    public Command parse(ArgumentList argList) {
        return new AddTaskCommand(String.join(" ", argList.getNormalArguments()), argList.getPropertyArguments());
    }

}
