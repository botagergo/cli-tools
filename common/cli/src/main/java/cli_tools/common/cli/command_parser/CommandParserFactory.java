package cli_tools.common.cli.command_parser;

import cli_tools.common.cli.argument.ArgumentList;

public interface CommandParserFactory {
    CommandParser getParser(ArgumentList argList);
}
