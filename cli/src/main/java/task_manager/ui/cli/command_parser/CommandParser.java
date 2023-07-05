package task_manager.ui.cli.command_parser;

import task_manager.ui.cli.Context;
import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.Command;

public interface CommandParser {
    Command parse(Context context, ArgumentList argList) throws CommandParserException;
}
