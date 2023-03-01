package task_manager.ui.cli.command_parser;

import task_manager.api.command.Command;

public interface CommandParser {
    public Command parse(ArgumentList argList);
}
