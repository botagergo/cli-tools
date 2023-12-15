package task_manager.ui.cli.command_parser;

import task_manager.ui.cli.argument.ArgumentList;

public interface CommandParserFactory {
    CommandParser getParser(ArgumentList argList);
}
