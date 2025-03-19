package common.music_cli.command_parser;

import common.cli.argument.ArgumentList;
import common.music_cli.Context;
import common.music_cli.command.ClearCommand;
import common.music_cli.command.Command;

public class ClearCommandParser implements CommandParser {

    @Override
    public Command parse(Context context, ArgumentList argList) {
        return new ClearCommand();
    }

}
