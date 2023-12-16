package task_manager.music_cli.command_parser;

import task_manager.cli_lib.argument.ArgumentList;
import task_manager.music_cli.Context;
import task_manager.music_cli.command.Command;

public interface CommandParser {
    Command parse(Context context, ArgumentList argList) throws CommandParserException;
}
