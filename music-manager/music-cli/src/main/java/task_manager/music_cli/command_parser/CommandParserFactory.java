package common.music_cli.command_parser;

import common.cli.argument.ArgumentList;

public interface CommandParserFactory {
    CommandParser getParser(ArgumentList argList);
}
