package task_manager.ui.cli.command_parser;

import task_manager.ui.cli.argument.ArgumentList;
import task_manager.ui.cli.command.Command;

public interface CommandParser {
    Command parse(ArgumentList argList);
}
