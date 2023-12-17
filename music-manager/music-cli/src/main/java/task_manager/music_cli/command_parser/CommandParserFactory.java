package task_manager.music_cli.command_parser;

import task_manager.cli_lib.argument.ArgumentList;

public interface CommandParserFactory {
    CommandParser getParser(ArgumentList argList);
}
