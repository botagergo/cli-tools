package cli_tools.task_manager.cli.command_parser;

import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.task_manager.cli.Context;
import cli_tools.task_manager.cli.command.Command;

public abstract class CommandParser {
    public abstract Command parse(Context context, ArgumentList argList) throws CommandParserException;
}
