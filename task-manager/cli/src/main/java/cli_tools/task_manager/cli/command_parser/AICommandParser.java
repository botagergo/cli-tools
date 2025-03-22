package cli_tools.task_manager.cli.command_parser;

import cli_tools.common.cli.Context;
import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.command_parser.CommandParser;
import cli_tools.task_manager.cli.command.AICommand;
import cli_tools.common.cli.command.Command;

public class AICommandParser extends CommandParser {
    @Override
    public Command parse(Context context, ArgumentList argList) {
        return new AICommand(String.join(" ", argList.getTrailingNormalArguments()));
    }
}
