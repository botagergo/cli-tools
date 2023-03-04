package task_manager.ui.cli.command_parser;

import task_manager.api.command.ClearCommand;
import task_manager.api.command.Command;
import task_manager.ui.cli.argument.ArgumentList;

public class ClearCommandParser implements CommandParser {

    @Override
    public Command parse(ArgumentList argList) {
        return new ClearCommand();
    }

}
