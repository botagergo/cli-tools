package task_manager.ui.cli.command_parser;

import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.Command;
import task_manager.ui.cli.command.ModifyTaskCommand;

public class ModifyTaskCommandParser implements CommandParser {

    @Override
    public Command parse(ArgumentList argList) {
        return new ModifyTaskCommand(String.join(" ", argList.getNormalArguments()), argList.getPropertyArguments());
    }

}
