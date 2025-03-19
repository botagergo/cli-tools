package common.music_cli.command_parser;

import common.cli.argument.ArgumentList;
import common.music_cli.Context;
import common.music_cli.command.Command;

public interface CommandParser {
    Command parse(Context context, ArgumentList argList) throws CommandParserException;
}
