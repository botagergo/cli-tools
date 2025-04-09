package cli_tools.task_manager.cli.command_parser;

import cli_tools.common.cli.Context;
import cli_tools.common.cli.argument.ArgumentList;
import cli_tools.common.cli.command.Command;
import cli_tools.common.cli.command_parser.CommandParser;
import cli_tools.common.cli.command_parser.CommandParserException;
import cli_tools.common.cli.command_parser.ParseUtil;
import cli_tools.task_manager.cli.command.UndoneTaskCommand;

public class UndoneTaskCommandParser extends CommandParser {

    @Override
    public Command parse(Context context, ArgumentList argList) throws CommandParserException {
        UndoneTaskCommand command = new UndoneTaskCommand();

        if (!argList.getModifyPropertyArguments().isEmpty()) {
            throw new CommandParserException("command 'undone' does not accept modify property arguments");
        } else if (!argList.getTrailingPositionalArguments().isEmpty()) {
            throw new CommandParserException("command 'undone' does not accept trailing positional arguments");
        } else if (!argList.getOptionArguments().isEmpty()) {
            throw new CommandParserException("command 'undone' does not accept option arguments");
        }

        command.setTempIDs(ParseUtil.getTempIds(context, argList.getLeadingPositionalArguments()));
        command.setFilterPropertyArgs(argList.getFilterPropertyArguments());

        return command;
    }

}
