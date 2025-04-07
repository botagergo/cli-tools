package cli_tools.task_manager.cli.command.custom_command;

import cli_tools.common.cli.Context;
import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.command.Command;
import cli_tools.common.cli.command.custom_command.CustomCommandParser;
import cli_tools.common.cli.command_parser.CommandParserException;
import cli_tools.common.cli.command_parser.ParseUtil;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BashCommandParser extends CustomCommandParser {

    @Override
    public Command parse(Context context, ArgumentList argList) throws CommandParserException {
        if (!argList.getTrailingPositionalArguments().isEmpty()) {
            throw new CommandParserException("Unexpected trailing arguments");
        } else if (!argList.getOptionArguments().isEmpty()) {
            throw new CommandParserException("Unexpected option arguments");
        } else if (!argList.getModifyPropertyArguments().isEmpty()) {
            throw new CommandParserException("Unexpected property arguments");
        }

        return new BashCommand(
                ParseUtil.getTempIds(context, argList.getLeadingPositionalArguments()),
                argList.getFilterPropertyArguments(),
                commandName,
                timeoutMillis
        );
    }

    private final String commandName;
    private final int timeoutMillis;

}
