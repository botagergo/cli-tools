package task_manager.task_manager_cli.command_parser;

import task_manager.cli_lib.argument.ArgumentList;
import task_manager.task_manager_cli.Context;
import task_manager.task_manager_cli.command.ClearCommand;
import task_manager.task_manager_cli.command.Command;

public class ClearCommandParser extends CommandParser {

    @Override
    public Command parse(Context context, ArgumentList argList) {
        return new ClearCommand();
    }

}
