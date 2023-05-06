package task_manager.ui.cli.command_parser;

import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.ClearCommand;
import task_manager.ui.cli.command.Command;

public class ClearCommandParser implements CommandParser {

    @Override
    public Command parse(ArgumentList argList) {
        return new ClearCommand();
    }

}
